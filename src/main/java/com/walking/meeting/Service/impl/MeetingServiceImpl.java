package com.walking.meeting.Service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.mapper.MeetingMapper;
import com.walking.meeting.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    public PageInfo<MeetingDO> listMeeting(Integer pageNum, Integer pageSize) {
        // pageSize==0时查全部
        PageHelper.startPage(pageNum, pageSize, true, null, true);
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingDO.class);
        List<MeetingDO> MeetingDOList = meetingMapper.selectByExample(builder.build());
        PageInfo<MeetingDO> pageInfo = new PageInfo<>(MeetingDOList);
        return pageInfo;
    }
}
