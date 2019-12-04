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
    public Boolean selectTimeByDateAndRoomID(String date,String roomId) {
        List<MeetingDTO> meetingDTOList = meetingMapper.selectTimeByDateAndRoomID(date,roomId);
        // 判断这个日期的这个room是否有空
        // 选出这个会议室的freeTime
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        MeetingRoomDO meetingRoomDO = managerService.getMeetingRoomByQuery(meetingRoomQuery);
        // 会议室freeTime转换成每天的时间!例如中午的 11：11：00 会变成 1111
        Date freeStartTime = meetingRoomDO.getFreeTimeStart();
        String everyDayFreeTimeStart = DateUtils.parseDateToEveryDayTime(freeStartTime);
        Date freeEndTime = meetingRoomDO.getFreeTimeEnd();
        String everyDayFreeTimeEnd = DateUtils.parseDateToEveryDayTime(freeEndTime);

        double totalFreeTime = DateUtils.getMeetingRequiredTime(freeStartTime, freeEndTime);
        // 如何meetingList里有开始时间早于freeStartTime结束时间迟于freeEndTime的就error
        //TODO 重写compareTo，转成String之间的比较共4位，前两位时，后两位分
        meetingDTOList.forEach(meetingDTO -> {
            // date1.compareTo(date2) ,date1<date2返回-1,date1>date2返回1，相等返回0
            if (timeCompare(meetingDTO.getBookingStartTime(),freeStartTime)<0) {
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_EARLY);
            }
            if (timeCompare(meetingDTO.getBookingEndTime(),freeEndTime)>0){
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_TOO_LATE);
            }
        });
        // 按时长来判断是否定满
        List<Double> MeetingTimeList = new ArrayList<>();
        double totalMeetingTime=0;
        meetingDTOList.forEach(meetingDTO -> {
            MeetingTimeList.add(DateUtils.getMeetingRequiredTime(meetingDTO.getBookingStartTime()
                    , meetingDTO.getBookingEndTime()));
        });
        for (int i = 0; i < MeetingTimeList.size(); i++) {
            totalMeetingTime += MeetingTimeList.get(i);
        }
        if (totalMeetingTime == totalFreeTime) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean isTimeAvailable(String startTime, String endTime, String date, String roomId) {
        List<MeetingDTO> meetingDTOList = meetingMapper.selectTimeByDateAndRoomID(date,roomId);
        Date sTime = DateUtils.parse(startTime,FORMAT_YYYY_MM_DD_HH_MM_SS);
        Date eTime = DateUtils.parse(endTime, FORMAT_YYYY_MM_DD_HH_MM_SS);
        meetingDTOList.forEach(meetingDTO -> {
            // 开始时间（！和！）结束时间都在时间段内timeCompare
            if (timeCompare(meetingDTO.getBookingStartTime(),sTime)<0 &&
                    timeCompare(meetingDTO.getBookingEndTime(),sTime)>0) {
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
            }
            if (timeCompare(meetingDTO.getBookingStartTime(),eTime) < 0 &&
                    timeCompare(meetingDTO.getBookingEndTime(),eTime) > 0) {
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
            }
            // 开始时间等于搜出来的开始时间（！或！）结束时间等于搜出来的结束时间，则一定重合
            if (timeCompare(meetingDTO.getBookingStartTime(),sTime) == 0 ||
                    timeCompare(meetingDTO.getBookingEndTime(),eTime) == 0){
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
            }
            // 上面两个比较，是定会议室时间和已存在会议室时间的比较
            // 下面的比较是，已存在会议室时间来比定会议室时间
            if (timeCompare(sTime,meetingDTO.getBookingStartTime()) < 0 &&
                    timeCompare(eTime,meetingDTO.getBookingEndTime()) >0) {
                throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
            }
        });
        return true;
    }

    @Override
    public List<Integer> searchDeviceByRoomId(String roomId) {
        List<RoomDeviceSearchResultDTO> roomDeviceSearchResultDTOList =
                roomDeviceMapper.meetingRoomSearchingByDevice(roomId);
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
