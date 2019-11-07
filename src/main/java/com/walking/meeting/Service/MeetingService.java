package com.walking.meeting.Service;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;

public interface MeetingService {

    void updateMeetingSelective(MeetingDO meetingDO);

    PageInfo<MeetingReturnDTO> listMeeting(ListMeetingDTO listMeetingDTO, Integer pageNum, Integer pageSize);

    void addMeeting(MeetingDTO meetingDTO);

}
