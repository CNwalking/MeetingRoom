package com.walking.meeting.Service;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.dataobject.dao.MeetingDO;

public interface MeetingService {

    void updateMeetingSelective(MeetingDO meetingDO);

    PageInfo<MeetingDO> listMeeting(Integer pageNum, Integer pageSize);

}
