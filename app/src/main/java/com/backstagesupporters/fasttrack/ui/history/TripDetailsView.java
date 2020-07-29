package com.backstagesupporters.fasttrack.ui.history;



import com.backstagesupporters.fasttrack.models.History;
import com.backstagesupporters.fasttrack.models.HistoryData;
import com.backstagesupporters.fasttrack.responseClass.HistoryReplyResponse;

import java.util.List;


public interface TripDetailsView  {

    void loadTripDetailData(List<HistoryData> dataHistory);
    void loadTripDetailResponse(HistoryReplyResponse data);
    void loadTripDetail(History data);


}
