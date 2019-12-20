package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.MeetingRoomDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceSearchResultDTO;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;
import com.walking.meeting.mapper.MeetingMapper;
import com.walking.meeting.mapper.RoomDeviceMapper;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.DbUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataUnit;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import static com.walking.meeting.utils.DateUtils.*;

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
    public Integer selectTimeByDateAndRoomID(String date,String roomId) {
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
        // 如果某个会议室当天可用时长小于2小时，则开始算法
        if (totalFreeTime-totalMeetingTime<=2){
            return 1;
        }
        if (totalMeetingTime == totalFreeTime) {
            return 2;
        }
        return 0;
    }

    @Override
    public Boolean isTimeAvailable(String startTime, String endTime, String date, String roomId) {
        // 先和会议室的freeTime比较，再和meeting列表里的会议比较
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        // roomId有错，没有这个room，不校验则下面会报空指针
        if (ObjectUtils.isEmpty(meetingRoomDO)){
            return false;
        }
        Date freeStartTime = meetingRoomDO.getFreeTimeStart();
        Date freeEndTime = meetingRoomDO.getFreeTimeEnd();
        if (timeCompare(DateUtils.parse(startTime,FORMAT_YYYY_MM_DD_HH_MM),freeStartTime)<0) {
//            throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_EARLY);
            return false;
        }
        if (timeCompare(DateUtils.parse(endTime,FORMAT_YYYY_MM_DD_HH_MM),freeEndTime)>0){
//            throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_LATE);
            return false;
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

}
