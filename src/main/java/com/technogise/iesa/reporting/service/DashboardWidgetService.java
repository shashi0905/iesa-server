package com.technogise.iesa.reporting.service;

import com.technogise.iesa.reporting.domain.Dashboard;
import com.technogise.iesa.reporting.domain.DashboardWidget;
import com.technogise.iesa.reporting.domain.Report;
import com.technogise.iesa.reporting.dto.CreateDashboardWidgetRequest;
import com.technogise.iesa.reporting.dto.DashboardWidgetDto;
import com.technogise.iesa.reporting.dto.DashboardWidgetMapper;
import com.technogise.iesa.reporting.repository.DashboardRepository;
import com.technogise.iesa.reporting.repository.DashboardWidgetRepository;
import com.technogise.iesa.reporting.repository.ReportRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardWidgetService {

    private final DashboardWidgetRepository widgetRepository;
    private final DashboardRepository dashboardRepository;
    private final ReportRepository reportRepository;
    private final DashboardWidgetMapper widgetMapper;

    @Transactional(readOnly = true)
    public List<DashboardWidgetDto> getAllWidgets() {
        return widgetMapper.toDtoList(widgetRepository.findAllNotDeleted());
    }

    @Transactional(readOnly = true)
    public DashboardWidgetDto getWidgetById(UUID id) {
        DashboardWidget widget = findWidgetById(id);
        return widgetMapper.toDto(widget);
    }

    @Transactional(readOnly = true)
    public List<DashboardWidgetDto> getWidgetsByDashboard(UUID dashboardId) {
        return widgetMapper.toDtoList(widgetRepository.findByDashboard(dashboardId));
    }

    public DashboardWidgetDto createWidget(UUID dashboardId, CreateDashboardWidgetRequest request) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard not found with id: " + dashboardId));

        Report report = reportRepository.findById(request.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + request.getReportId()));

        DashboardWidget widget = DashboardWidget.builder()
                .dashboard(dashboard)
                .report(report)
                .title(request.getTitle())
                .position(request.getPosition())
                .refreshInterval(request.getRefreshInterval())
                .width(request.getWidth())
                .height(request.getHeight())
                .orderIndex(request.getOrderIndex())
                .build();

        DashboardWidget savedWidget = widgetRepository.save(widget);
        return widgetMapper.toDto(savedWidget);
    }

    public DashboardWidgetDto updateWidget(UUID id, CreateDashboardWidgetRequest request) {
        DashboardWidget widget = findWidgetById(id);

        if (request.getReportId() != null) {
            Report report = reportRepository.findById(request.getReportId())
                    .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + request.getReportId()));
            widget.setReport(report);
        }
        if (request.getTitle() != null) {
            widget.setTitle(request.getTitle());
        }
        if (request.getPosition() != null) {
            widget.setPosition(request.getPosition());
        }
        if (request.getRefreshInterval() != null) {
            widget.setRefreshInterval(request.getRefreshInterval());
        }
        if (request.getWidth() != null) {
            widget.setWidth(request.getWidth());
        }
        if (request.getHeight() != null) {
            widget.setHeight(request.getHeight());
        }
        if (request.getOrderIndex() != null) {
            widget.setOrderIndex(request.getOrderIndex());
        }

        DashboardWidget updatedWidget = widgetRepository.save(widget);
        return widgetMapper.toDto(updatedWidget);
    }

    public void deleteWidget(UUID id) {
        DashboardWidget widget = findWidgetById(id);
        widget.setDeletedAt(Instant.now());
        widgetRepository.save(widget);
    }

    private DashboardWidget findWidgetById(UUID id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard widget not found with id: " + id));
    }
}
