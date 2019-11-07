package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.mapper.MeetingMapper;
import com.walking.meeting.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private MeetingMapper meetingMapper;

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
    public PageInfo<MeetingReturnDTO> listMeeting(ListMeetingDTO listMeetingDTO, Integer pageNum, Integer pageSize) {
        // pageSize==0时查全部
        PageHelper.startPage(pageNum, pageSize, true, null, true);
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingDO.class);
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_USERNAME, listMeetingDTO.getUsername());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_REQUIRED_TIME, listMeetingDTO.getRequiredTime());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_BOOKING_DATE, listMeetingDTO.getBookingDate());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_MEETING_LEVEL, listMeetingDTO.getMeetingLevel());
        DbUtils.setEqualToProp(builder, MeetingDO.PROP_DEPARTMENT_NAME, listMeetingDTO.getDepartmentName());
        List<MeetingDO> meetingDOList = meetingMapper.selectByExample(builder.build());
        // TODO 把meetingDO转化为meetingReturnDTO
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

}
