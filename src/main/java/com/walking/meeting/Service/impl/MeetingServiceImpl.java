package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.Const;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.MeetingRoomDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceSearchResultDTO;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;
import com.walking.meeting.dataobject.vo.MeetingRoomVO;
import com.walking.meeting.mapper.MeetingMapper;
import com.walking.meeting.mapper.RoomDeviceMapper;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.unit.DataUnit;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import static com.walking.meeting.utils.DateUtils.*;

@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private ManagerServiceImpl managerService;
    @Autowired
    private RoomDeviceMapper roomDeviceMapper;


    @Override
    public void updateMeetingSelective(MeetingDO meetingDO) {
        if(Objects.isNull(meetingDO.getMeetingId())){
            return;
        }
        // 根据meetingId更新字段
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingDO.class);
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_MEETING_ID, meetingDO.getMeetingId());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_USERNAME, meetingDO.getUsername());
        meetingDO.setUpdateTime(new Date());
        meetingMapper.updateByExampleSelective(meetingDO,builder.build());
    }

    @Override
    public PageInfo<MeetingReturnDTO> listMeeting(ListMeetingDTO listMeetingDTO) {
        // pageSize==0时查全部
        PageHelper.startPage(listMeetingDTO.getPageNum(), listMeetingDTO.getPageSize(),
                true, null, true);
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingDO.class);
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_USERNAME, listMeetingDTO.getUsername());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_REQUIRED_TIME, listMeetingDTO.getRequiredTime());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_BOOKING_DATE, listMeetingDTO.getBookingDate());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_MEETING_LEVEL, listMeetingDTO.getMeetingLevel());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_DEPARTMENT_NAME, listMeetingDTO.getDepartmentName());
        List<MeetingDO> meetingDOList = meetingMapper.selectByExample(builder.build());
        // 把meetingDO转化为meetingReturnDTO
        List<MeetingReturnDTO> meetingReturnDTOList = new ArrayList<>();
        meetingDOList.forEach(meetingDO -> {
            meetingReturnDTOList.add(JSON.parseObject(JSON.toJSONString(meetingDO), MeetingReturnDTO.class));
        });
        PageInfo<MeetingReturnDTO> pageInfo = new PageInfo<>(meetingReturnDTOList);
        return pageInfo;
    }

    @Override
    public void addMeeting(MeetingDTO meetingDTO) {
        MeetingDO meetingDO =  JSON.parseObject(JSON.toJSONString(meetingDTO), MeetingDO.class);
        meetingDO.setCreateTime(new Date());
        meetingMapper.insertSelective(meetingDO);
    }

    @Override
    public MeetingDO searchMeetingByMeetingId(String meetingId) {
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingDO.class);
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_MEETING_ID, meetingId);
        List<MeetingDO> listMeetingDOList = meetingMapper.selectByExample(builder.build());
        return DbUtils.getOne(listMeetingDOList).orElse(null);
    }

    @Override
    public String selectTimeByDateAndRoomID(String date,String roomId) {
        List<MeetingDTO> meetingDTOList = meetingMapper.selectTimeByDateAndRoomID(date,roomId);
        // 判断这个日期的这个room是否有空
        // 取出这个会议室的freeTime
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        // 不能通过会议室Id搜出来则会议室不存在
        if (ObjectUtils.isEmpty(meetingRoomDO)) {
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_NOT_EXIST);
        }
        Date freeStartTime = meetingRoomDO.getFreeTimeStart();
        Date freeEndTime = meetingRoomDO.getFreeTimeEnd();
        // 转成小时数，用于后面比较是否有空
        double totalFreeTime = DateUtils.getMeetingRequiredTime(freeStartTime, freeEndTime);
        // TODO 此处比较存疑，是否需要在这里比较？ 先前想的是这里取出了MeetingRoom的属性，就在这里比较了
        // TODO 此处有问题，此处只检测了数据库里是否有超时的，没有先检测要定的那个时间是否超过，应修改。
        // 如果meetingList里有开始时间早于freeStartTime结束时间迟于freeEndTime的就error
        // timeCompare方法，转成Integer之间的比较共4位，前两位时，后两位分
        meetingDTOList.forEach(meetingDTO -> {
            // 会议室freeTime转换成每天的时间再进行比较!例如中午的 11：11：00 会变成 1111
            // timeCompare(date1，date2) ,date1<date2返回-1,date1>date2返回1,相等返回0
            if (timeCompare(meetingDTO.getBookingStartTime(),freeStartTime)<0) {
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_EARLY);
            }
            if (timeCompare(meetingDTO.getBookingEndTime(),freeEndTime)>0){
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_LATE);
            }
        });
        // 按时长来判断是否定满
        List<Double> MeetingTimeList = new ArrayList<>();
        // 一个会议室在当天的已预订时长
        double totalMeetingTime=0;
        // 每个会议的时长的一个list
        meetingDTOList.forEach(meetingDTO -> {
            MeetingTimeList.add(DateUtils.getMeetingRequiredTime(meetingDTO.getBookingStartTime()
                    , meetingDTO.getBookingEndTime()));
        });
        for (int i = 0; i < MeetingTimeList.size(); i++) {
            totalMeetingTime += MeetingTimeList.get(i);
        }
        // 如果某个会议室当天可用时长小于2小时，则进入候补队列开始算法,显示红色
        if (totalFreeTime-totalMeetingTime<=2){
            return Const.ROOM_CROWDED;
        }
        // 还剩2到4小时，就显示黄色
        if (totalFreeTime - totalMeetingTime <= 4 && totalFreeTime - totalMeetingTime >= 2) {
            return Const.ROOM_JUST_SOSO;
        }
        if (totalMeetingTime == totalFreeTime) {
            return Const.ROOM_FULL_TIME;
        }
        return Const.ROOM_AVAILABLE;
    }

    @Override
    public Boolean isTimeAvailable(String startTime, String endTime, String date, String roomId) {
        log.info("时间合理吗会议开始时间:{},会议结束时间:{},会议日期:{},会议室:{}",startTime,endTime,date,roomId);
        // 先和会议室的freeTime比较，再和meeting列表里的会议比较
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        // roomId有错，没有这个room，不校验则下面会报空指针
        if (ObjectUtils.isEmpty(meetingRoomDO)){
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_NOT_EXIST);
//            return false;
        }
        Date freeStartTime = meetingRoomDO.getFreeTimeStart();
        Date freeEndTime = meetingRoomDO.getFreeTimeEnd();
        if (timeCompare(DateUtils.parse(startTime,FORMAT_YYYY_MM_DD_HH_MM),freeStartTime)<0) {
            throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_EARLY);
//            return false;
        }
        if (timeCompare(DateUtils.parse(endTime,FORMAT_YYYY_MM_DD_HH_MM),freeEndTime)>0){
            throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_LATE);
//            return false;
        }
        // 以下穷举所有会议预定情况判断
        List<MeetingDTO> meetingDTOList = meetingMapper.selectTimeByDateAndRoomID(date,roomId);
        Date sTime = DateUtils.parse(startTime,FORMAT_YYYY_MM_DD_HH_MM);
        Date eTime = DateUtils.parse(endTime, FORMAT_YYYY_MM_DD_HH_MM);
        // lambda表达式的forEach不是循环，不能break或continue或return来终止
//        meetingDTOList.forEach(meetingDTO -> {
//            // 开始时间（！和！）结束时间都在时间段内timeCompare
//            if (timeCompare(meetingDTO.getBookingStartTime(),sTime)<0 &&
//                    timeCompare(meetingDTO.getBookingEndTime(),sTime)>0) {
//                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
//            }
//            if (timeCompare(meetingDTO.getBookingStartTime(),eTime) < 0 &&
//                    timeCompare(meetingDTO.getBookingEndTime(),eTime) > 0) {
//                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
//            }
//            // 开始时间等于搜出来的开始时间（！或！）结束时间等于搜出来的结束时间，则一定重合
//            if (timeCompare(meetingDTO.getBookingStartTime(),sTime) == 0 ||
//                    timeCompare(meetingDTO.getBookingEndTime(),eTime) == 0){
//                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
//            }
//            // 上面两个比较，是定会议室时间和已存在会议室时间的比较
//            // 下面的比较是，已存在会议室时间来比定会议室时间
//            if (timeCompare(sTime,meetingDTO.getBookingStartTime()) < 0 &&
//                    timeCompare(eTime,meetingDTO.getBookingEndTime()) >0) {
//                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
//            }
//        });
        // 用for循环可以return false
        // TODO 这个meetingDTOList是空的？？BUG
        if (!CollectionUtils.isEmpty(meetingDTOList)) {
            for (int i = 0; i < meetingDTOList.size(); i++) {
                if (timeCompare(meetingDTOList.get(i).getBookingStartTime(),sTime)<0 &&
                        timeCompare(meetingDTOList.get(i).getBookingEndTime(),sTime)>0) {
                    return false;
                }
                if (timeCompare(meetingDTOList.get(i).getBookingStartTime(),eTime)<0 &&
                        timeCompare(meetingDTOList.get(i).getBookingEndTime(),eTime)>0) {
                    return false;
                }
                if (timeCompare(meetingDTOList.get(i).getBookingStartTime(),sTime) == 0 ||
                        timeCompare(meetingDTOList.get(i).getBookingEndTime(),eTime) == 0) {
                    return false;
                }
                if (timeCompare(sTime,meetingDTOList.get(i).getBookingStartTime()) < 0 &&
                        timeCompare(eTime,meetingDTOList.get(i).getBookingEndTime()) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Integer> searchDeviceByRoomId(String roomId) {
        List<RoomDeviceSearchResultDTO> roomDeviceSearchResultDTOList =
                roomDeviceMapper.searchDeviceByRoomId(roomId);
        List<Integer> MeetingRoomDeviceList = new ArrayList<>();
        roomDeviceSearchResultDTOList.forEach(roomDeviceSearchResultDTO -> {
                if (StringUtils.isNotBlank(roomDeviceSearchResultDTO.getDeviceIdList())) {
                    Arrays.asList(roomDeviceSearchResultDTO.getDeviceIdList().split(",")).forEach(
                            deviceId ->{
                                MeetingRoomDeviceList.add(Integer.parseInt(deviceId));
                            }
                    );
                }
        });
        return MeetingRoomDeviceList;
    }

    @Override
    public PageInfo<MeetingRoomVO> searchRoomByQuery(String bookingDate,String deviceIdList, Integer roomScale, Integer pageNum, Integer pageSize) {
        // pageSize==0时查全部
        PageHelper.startPage(pageNum, pageSize, true, null, true);
        // TODO 合起来写报错...  figure out 有什么问题
//        List<RoomDeviceSearchResultDTO> roomDeviceSearchResultDTOList = roomDeviceMapper.searchRoomIdByDevice(roomScale);
//        分开写，先通过scale来搜roomId,再用roomId来搜list
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        short scale = Short.valueOf(String.valueOf(roomScale));
        meetingRoomQuery.setRoomScale(scale);
        List<MeetingRoomDO> meetingRoomDOList = managerService.getMeetingRoomByQuery(meetingRoomQuery);
//        log.info(meetingRoomDOList.toString());
        // 通过roomId来搜它的设备list,搜出的结果存到 HashMap<roomId,deviceList>
        Map<String, String> roomDeviceMap = new HashMap<>();
        meetingRoomDOList.forEach(meetingRoomDO -> {
            roomDeviceMapper.searchDeviceByRoomId(meetingRoomDO.getRoomId()).forEach(
                    ResultDTO -> {
                        roomDeviceMap.put(ResultDTO.getRoomId(), ResultDTO.getDeviceIdList());
                    }
            );
        });
//        log.info(roomDeviceMap.toString());
        // Map的结果{6-25=1,2,3,4,5, 4-07=1,2,3,4, 6-21=1,2,3,4}，需要进行判定来选取合适的几个MeetingRoom
        MeetingRoomQuery meetingRoomQuery2 = new MeetingRoomQuery();
        List<MeetingRoomDO> resultList = new ArrayList<>();
        Set<Map.Entry<String, String>> entrySet = roomDeviceMap.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String roomId = next.getKey();
            String deviceList = next.getValue();
            if (isContainDevice(deviceList, deviceIdList)) {
                meetingRoomQuery2.setRoomId(roomId);
                resultList.add(DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery2)).orElse(null));
            }
        }
        List<MeetingRoomVO> VOList = new ArrayList<>();
        resultList.forEach(meetingRoomDO -> {
//            // 时间转化成看的清楚一些的时间，例如18：00
//            String StartTime = DateUtils.formatDate(meetingRoomDO.getFreeTimeStart(),SHOWTIME);
//            String endTime = DateUtils.formatDate(meetingRoomDO.getFreeTimeEnd(),SHOWTIME);
            String isBusy = selectTimeByDateAndRoomID(bookingDate, meetingRoomDO.getRoomId());
            MeetingRoomVO meetingRoomVO = JSON.parseObject(JSON.toJSONString(meetingRoomDO), MeetingRoomVO.class);
            meetingRoomVO.setFreeTimeStart(meetingRoomDO.getFreeTimeStart());
            meetingRoomVO.setFreeTimeEnd(meetingRoomDO.getFreeTimeEnd());
            if (isBusy == Const.ROOM_CROWDED) {
                meetingRoomVO.setBusyOrNot(8);
            }
            if (isBusy == Const.ROOM_JUST_SOSO) {
                meetingRoomVO.setBusyOrNot(6);
            }
            if (isBusy == Const.ROOM_AVAILABLE) {
                meetingRoomVO.setBusyOrNot(1);
            }
            VOList.add(meetingRoomVO);
        });
        PageInfo<MeetingRoomVO> pageInfo = new PageInfo<>(VOList);
        return pageInfo;
    }

    private Boolean isContainDevice(String roomDevice, String needDevice) {
        List<String> needDeviceList = new ArrayList();
        if (StringUtils.isNotBlank(needDevice)) {
            needDeviceList = Arrays.asList(needDevice.split(","));
        }
        for (int i = 0; i < needDeviceList.size(); i++) {
            // 如果等于-1，说明没有这个device
            if(roomDevice.indexOf(needDeviceList.get(i)) == -1){
                return false;
            }
        }
        return true;
    }
}
