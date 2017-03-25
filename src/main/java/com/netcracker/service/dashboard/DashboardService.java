package com.netcracker.service.dashboard;


import com.netcracker.model.dto.Dashboard;

import java.security.Principal;

public interface DashboardService {

    Dashboard getData(Principal principal);

    Dashboard getDataByUser(Long userId);

}
