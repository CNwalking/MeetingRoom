package com.walking.meeting.Service;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.MeetingRoomDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceSearchResultDTO;

import java.util.List;

public interface MeetingService {

    void updateMeetingSelective(MeetingDO meetingDO);

    PageInfo<MeetingReturnDTO> listMeeting(ListMeetingDTO listMeetingDTO);

    void addMeeting(MeetingDTO meetingDTO);

    MeetingDO searchMeetingByMeetingId(String meetingId);

    String selectTimeByDateAndRoomID(String date,String roomId);

    Boolean isTimeAvailable(String startTime,String endTime,String date,String roomId);

    List<Integer> searchDeviceByRoomId(String roomId);


}
