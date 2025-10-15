package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.ApprovalStep;
import com.technogise.iesa.approvalworkflow.domain.ApprovalWorkflow;
import com.technogise.iesa.approvalworkflow.dto.*;
import com.technogise.iesa.approvalworkflow.repository.ApprovalStepRepository;
import com.technogise.iesa.approvalworkflow.repository.ApprovalWorkflowRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.Role;
import com.technogise.iesa.usermanagement.domain.RoleType;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.RoleRepository;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalWorkflowServiceTest {

    @Mock
    private ApprovalWorkflowRepository workflowRepository;

    @Mock
    private ApprovalStepRepository stepRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApprovalWorkflowMapper mapper;

    @InjectMocks
    private ApprovalWorkflowService workflowService;

    private ApprovalWorkflow workflow;
    private ApprovalWorkflowDto workflowDto;
    private ApprovalStep step;
    private ApprovalStepDto stepDto;
    private CreateApprovalWorkflowRequest createRequest;
    private UpdateApprovalWorkflowRequest updateRequest;
    private UUID workflowId;
    private UUID roleId;
    private UUID userId;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        workflowId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        userId = UUID.randomUUID();

        role = Role.builder()
                .id(roleId)
                .roleType(RoleType.MANAGER)
                .build();

        user = User.builder()
                .id(userId)
                .username("approver")
                .email("approver@example.com")
                .firstName("Approver")
                .lastName("User")
                .build();

        step = ApprovalStep.builder()
                .id(UUID.randomUUID())
                .stepOrder(1)
                .approverRole(role)
                .isMandatory(true)
                .stepName("Manager Approval")
                .build();

        workflow = ApprovalWorkflow.builder()
                .id(workflowId)
                .name("Simple Approval")
                .description("Single step approval workflow")
                .isActive(true)
                .build();
        workflow.addStep(step);

        stepDto = ApprovalStepDto.builder()
                .id(step.getId())
                .workflowId(workflowId)
                .stepOrder(1)
                .approverRoleId(roleId)
                .approverRoleName("MANAGER")
                .isMandatory(true)
                .stepName("Manager Approval")
                .build();

        workflowDto = ApprovalWorkflowDto.builder()
                .id(workflowId)
                .name("Simple Approval")
                .description("Single step approval workflow")
                .isActive(true)
                .steps(Collections.singletonList(stepDto))
                .build();

        ApprovalStepRequest stepRequest = ApprovalStepRequest.builder()
                .stepOrder(1)
                .approverRoleId(roleId)
                .isMandatory(true)
                .stepName("Manager Approval")
                .build();

        createRequest = CreateApprovalWorkflowRequest.builder()
                .name("Simple Approval")
                .description("Single step approval workflow")
                .steps(Collections.singletonList(stepRequest))
                .build();

        updateRequest = UpdateApprovalWorkflowRequest.builder()
                .name("Updated Approval")
                .description("Updated workflow description")
                .build();
    }

    @Test
    void getAllWorkflows_ShouldReturnAllWorkflows() {
        // Arrange
        List<ApprovalWorkflow> workflows = Arrays.asList(workflow);
        List<ApprovalWorkflowDto> workflowDtos = Arrays.asList(workflowDto);

        when(workflowRepository.findAllNotDeleted()).thenReturn(workflows);
        when(mapper.toWorkflowDtoList(workflows)).thenReturn(workflowDtos);

        // Act
        List<ApprovalWorkflowDto> result = workflowService.getAllWorkflows();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Simple Approval");
        verify(workflowRepository, times(1)).findAllNotDeleted();
        verify(mapper, times(1)).toWorkflowDtoList(workflows);
    }

    @Test
    void getAllActiveWorkflows_ShouldReturnOnlyActiveWorkflows() {
        // Arrange
        List<ApprovalWorkflow> workflows = Arrays.asList(workflow);
        List<ApprovalWorkflowDto> workflowDtos = Arrays.asList(workflowDto);

        when(workflowRepository.findAllActive()).thenReturn(workflows);
        when(mapper.toWorkflowDtoList(workflows)).thenReturn(workflowDtos);

        // Act
        List<ApprovalWorkflowDto> result = workflowService.getAllActiveWorkflows();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        verify(workflowRepository, times(1)).findAllActive();
    }

    @Test
    void getWorkflowById_WhenExists_ShouldReturnWorkflow() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.getWorkflowById(workflowId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(workflowId);
        assertThat(result.getName()).isEqualTo("Simple Approval");
        verify(workflowRepository, times(1)).findById(workflowId);
    }

    @Test
    void getWorkflowById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workflowService.getWorkflowById(workflowId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workflow not found with id");

        verify(workflowRepository, times(1)).findById(workflowId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getWorkflowByName_WhenExists_ShouldReturnWorkflow() {
        // Arrange
        when(workflowRepository.findByName("Simple Approval")).thenReturn(Optional.of(workflow));
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.getWorkflowByName("Simple Approval");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Simple Approval");
        verify(workflowRepository, times(1)).findByName("Simple Approval");
    }

    @Test
    void getWorkflowByName_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(workflowRepository.findByName("Unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workflowService.getWorkflowByName("Unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workflow not found with name");

        verify(workflowRepository, times(1)).findByName("Unknown");
    }

    @Test
    void createWorkflow_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        when(workflowRepository.existsByName("Simple Approval")).thenReturn(false);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(workflow);
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.createWorkflow(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Simple Approval");
        verify(workflowRepository, times(1)).existsByName("Simple Approval");
        verify(roleRepository, times(1)).findById(roleId);
        verify(workflowRepository, times(1)).save(any(ApprovalWorkflow.class));
    }

    @Test
    void createWorkflow_WithDuplicateName_ShouldThrowException() {
        // Arrange
        when(workflowRepository.existsByName("Simple Approval")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> workflowService.createWorkflow(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(workflowRepository, times(1)).existsByName("Simple Approval");
        verify(workflowRepository, never()).save(any());
    }

    @Test
    void createWorkflow_WithoutSteps_ShouldCreateSuccessfully() {
        // Arrange
        CreateApprovalWorkflowRequest requestWithoutSteps = CreateApprovalWorkflowRequest.builder()
                .name("Simple Approval")
                .description("Workflow without steps")
                .build();

        ApprovalWorkflow workflowWithoutSteps = ApprovalWorkflow.builder()
                .id(workflowId)
                .name("Simple Approval")
                .description("Workflow without steps")
                .isActive(true)
                .build();

        when(workflowRepository.existsByName("Simple Approval")).thenReturn(false);
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(workflowWithoutSteps);
        when(mapper.toDto(workflowWithoutSteps)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.createWorkflow(requestWithoutSteps);

        // Assert
        assertThat(result).isNotNull();
        verify(workflowRepository, times(1)).save(any(ApprovalWorkflow.class));
    }

    @Test
    void updateWorkflow_WithValidData_ShouldUpdateSuccessfully() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.existsByName("Updated Approval")).thenReturn(false);
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(workflow);
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.updateWorkflow(workflowId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, times(1)).save(any(ApprovalWorkflow.class));
    }

    @Test
    void updateWorkflow_WithDuplicateName_ShouldThrowException() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.existsByName("Updated Approval")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> workflowService.updateWorkflow(workflowId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, never()).save(any());
    }

    @Test
    void updateWorkflow_SameNameAsExisting_ShouldUpdateSuccessfully() {
        // Arrange
        UpdateApprovalWorkflowRequest sameNameRequest = UpdateApprovalWorkflowRequest.builder()
                .name("Simple Approval")
                .description("Updated description")
                .build();

        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.save(workflow)).thenReturn(workflow);
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.updateWorkflow(workflowId, sameNameRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, never()).existsByName(anyString());
        verify(workflowRepository, times(1)).save(workflow);
    }

    @Test
    void deleteWorkflow_WhenExists_ShouldSoftDelete() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));

        // Act
        workflowService.deleteWorkflow(workflowId);

        // Assert
        assertThat(workflow.getDeletedAt()).isNotNull();
        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, times(1)).save(workflow);
    }

    @Test
    void deleteWorkflow_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workflowService.deleteWorkflow(workflowId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workflow not found");

        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, never()).save(any());
    }

    @Test
    void activateWorkflow_WhenExists_ShouldActivate() {
        // Arrange
        workflow.deactivate();
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.save(workflow)).thenReturn(workflow);
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.activateWorkflow(workflowId);

        // Assert
        assertThat(workflow.getIsActive()).isTrue();
        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, times(1)).save(workflow);
    }

    @Test
    void deactivateWorkflow_WhenExists_ShouldDeactivate() {
        // Arrange
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.save(workflow)).thenReturn(workflow);
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.deactivateWorkflow(workflowId);

        // Assert
        assertThat(workflow.getIsActive()).isFalse();
        verify(workflowRepository, times(1)).findById(workflowId);
        verify(workflowRepository, times(1)).save(workflow);
    }

    @Test
    void getWorkflowSteps_ShouldReturnSteps() {
        // Arrange
        List<ApprovalStep> steps = Arrays.asList(step);
        List<ApprovalStepDto> stepDtos = Arrays.asList(stepDto);

        when(stepRepository.findByWorkflowIdOrderByStepOrder(workflowId)).thenReturn(steps);
        when(mapper.toStepDtoList(steps)).thenReturn(stepDtos);

        // Act
        List<ApprovalStepDto> result = workflowService.getWorkflowSteps(workflowId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStepName()).isEqualTo("Manager Approval");
        verify(stepRepository, times(1)).findByWorkflowIdOrderByStepOrder(workflowId);
    }

    @Test
    void getMandatorySteps_ShouldReturnOnlyMandatorySteps() {
        // Arrange
        List<ApprovalStep> steps = Arrays.asList(step);
        List<ApprovalStepDto> stepDtos = Arrays.asList(stepDto);

        when(stepRepository.findMandatoryStepsByWorkflowId(workflowId)).thenReturn(steps);
        when(mapper.toStepDtoList(steps)).thenReturn(stepDtos);

        // Act
        List<ApprovalStepDto> result = workflowService.getMandatorySteps(workflowId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isMandatory()).isTrue();
        verify(stepRepository, times(1)).findMandatoryStepsByWorkflowId(workflowId);
    }

    @Test
    void createWorkflow_WithUserApprover_ShouldCreateSuccessfully() {
        // Arrange
        ApprovalStepRequest stepWithUserRequest = ApprovalStepRequest.builder()
                .stepOrder(1)
                .approverUserId(userId)
                .isMandatory(true)
                .stepName("User Approval")
                .build();

        CreateApprovalWorkflowRequest requestWithUser = CreateApprovalWorkflowRequest.builder()
                .name("User Approval Workflow")
                .description("Workflow with user approver")
                .steps(Collections.singletonList(stepWithUserRequest))
                .build();

        when(workflowRepository.existsByName("User Approval Workflow")).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workflowRepository.save(any(ApprovalWorkflow.class))).thenReturn(workflow);
        when(mapper.toDto(workflow)).thenReturn(workflowDto);

        // Act
        ApprovalWorkflowDto result = workflowService.createWorkflow(requestWithUser);

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findById(userId);
        verify(workflowRepository, times(1)).save(any(ApprovalWorkflow.class));
    }

    @Test
    void createWorkflow_WithInvalidRole_ShouldThrowException() {
        // Arrange
        UUID invalidRoleId = UUID.randomUUID();
        ApprovalStepRequest stepWithInvalidRole = ApprovalStepRequest.builder()
                .stepOrder(1)
                .approverRoleId(invalidRoleId)
                .isMandatory(true)
                .build();

        CreateApprovalWorkflowRequest invalidRequest = CreateApprovalWorkflowRequest.builder()
                .name("Invalid Workflow")
                .steps(Collections.singletonList(stepWithInvalidRole))
                .build();

        when(workflowRepository.existsByName("Invalid Workflow")).thenReturn(false);
        when(roleRepository.findById(invalidRoleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workflowService.createWorkflow(invalidRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role not found");

        verify(workflowRepository, never()).save(any());
    }

    @Test
    void createWorkflow_WithInvalidUser_ShouldThrowException() {
        // Arrange
        UUID invalidUserId = UUID.randomUUID();
        ApprovalStepRequest stepWithInvalidUser = ApprovalStepRequest.builder()
                .stepOrder(1)
                .approverUserId(invalidUserId)
                .isMandatory(true)
                .build();

        CreateApprovalWorkflowRequest invalidRequest = CreateApprovalWorkflowRequest.builder()
                .name("Invalid Workflow")
                .steps(Collections.singletonList(stepWithInvalidUser))
                .build();

        when(workflowRepository.existsByName("Invalid Workflow")).thenReturn(false);
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workflowService.createWorkflow(invalidRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(workflowRepository, never()).save(any());
    }
}
