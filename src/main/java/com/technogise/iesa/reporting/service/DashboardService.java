package com.technogise.iesa.reporting.service;

import com.technogise.iesa.reporting.domain.Dashboard;
import com.technogise.iesa.reporting.dto.CreateDashboardRequest;
import com.technogise.iesa.reporting.dto.DashboardDto;
import com.technogise.iesa.reporting.dto.DashboardMapper;
import com.technogise.iesa.reporting.dto.UpdateDashboardRequest;
import com.technogise.iesa.reporting.repository.DashboardRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;

    @Transactional(readOnly = true)
    public List<DashboardDto> getAllDashboards() {
        return dashboardMapper.toDtoList(dashboardRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboardById(UUID id) {
        Dashboard dashboard = findDashboardById(id);
        return dashboardMapper.toDto(dashboard);
    }

    @Transactional(readOnly = true)
    public List<DashboardDto> getDashboardsByOwner(UUID ownerId) {
        return dashboardMapper.toDtoList(dashboardRepository.findAllByOwner(ownerId));
    }

    @Transactional(readOnly = true)
    public DashboardDto getDefaultDashboard(UUID ownerId) {
        Dashboard dashboard = dashboardRepository.findDefaultByOwner(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Default dashboard not found for user: " + ownerId));
        return dashboardMapper.toDto(dashboard);
    }

    @Transactional(readOnly = true)
    public List<DashboardDto> getSharedDashboards() {
        return dashboardMapper.toDtoList(dashboardRepository.findSharedDashboards());
    }

    public DashboardDto createDashboard(CreateDashboardRequest request, UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

        // If this is set as default, unset other default dashboards
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            dashboardRepository.findDefaultByOwner(ownerId)
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        dashboardRepository.save(existing);
                    });
        }

        Dashboard dashboard = Dashboard.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .layout(request.getLayout())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .isShared(request.getIsShared() != null ? request.getIsShared() : false)
                .build();

        Dashboard savedDashboard = dashboardRepository.save(dashboard);
        return dashboardMapper.toDto(savedDashboard);
    }

    public DashboardDto updateDashboard(UUID id, UpdateDashboardRequest request) {
        Dashboard dashboard = findDashboardById(id);

        if (request.getName() != null) {
            dashboard.setName(request.getName());
        }
        if (request.getDescription() != null) {
            dashboard.setDescription(request.getDescription());
        }
        if (request.getLayout() != null) {
            dashboard.setLayout(request.getLayout());
        }
        if (request.getIsDefault() != null) {
            // If setting as default, unset other default dashboards
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                dashboardRepository.findDefaultByOwner(dashboard.getOwner().getId())
                        .ifPresent(existing -> {
                            if (!existing.getId().equals(id)) {
                                existing.setIsDefault(false);
                                dashboardRepository.save(existing);
                            }
                        });
            }
            dashboard.setIsDefault(request.getIsDefault());
        }
        if (request.getIsShared() != null) {
            dashboard.setIsShared(request.getIsShared());
        }

        Dashboard updatedDashboard = dashboardRepository.save(dashboard);
        return dashboardMapper.toDto(updatedDashboard);
    }

    public void deleteDashboard(UUID id) {
        Dashboard dashboard = findDashboardById(id);
        dashboard.setDeletedAt(Instant.now());
        dashboardRepository.save(dashboard);
    }

    private Dashboard findDashboardById(UUID id) {
        return dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard not found with id: " + id));
    }
}
