package com.technogise.iesa.approvalworkflow.dto;

import com.technogise.iesa.approvalworkflow.domain.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalWorkflowMapper {

    // ApprovalWorkflow mappings
    ApprovalWorkflowDto toDto(ApprovalWorkflow workflow);

    List<ApprovalWorkflowDto> toWorkflowDtoList(List<ApprovalWorkflow> workflows);

    // ApprovalStep mappings
    @Mapping(target = "workflowId", source = "workflow.id")
    @Mapping(target = "approverRoleId", source = "approverRole.id")
    @Mapping(target = "approverRoleName", source = "approverRole.name")
    @Mapping(target = "approverUserId", source = "approverUser.id")
    @Mapping(target = "approverUserName", expression = "java(step.getApproverUser() != null ? step.getApproverUser().getFullName() : null)")
    ApprovalStepDto toDto(ApprovalStep step);

    List<ApprovalStepDto> toStepDtoList(List<ApprovalStep> steps);

    // ApprovalAction mappings
    @Mapping(target = "expenseId", source = "expense.id")
    @Mapping(target = "stepId", source = "step.id")
    @Mapping(target = "stepName", source = "step.stepName")
    @Mapping(target = "approverId", source = "approver.id")
    @Mapping(target = "approverName", expression = "java(action.getApprover().getFullName())")
    @Mapping(target = "action", expression = "java(action.getAction().name())")
    @Mapping(target = "delegatedToId", source = "delegatedTo.id")
    @Mapping(target = "delegatedToName", expression = "java(action.getDelegatedTo() != null ? action.getDelegatedTo().getFullName() : null)")
    ApprovalActionDto toDto(ApprovalAction action);

    List<ApprovalActionDto> toActionDtoList(List<ApprovalAction> actions);

    // Comment mappings
    @Mapping(target = "expenseId", source = "expense.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getFullName())")
    CommentDto toDto(Comment comment);

    List<CommentDto> toCommentDtoList(List<Comment> comments);

    // WorkflowHistory mappings
    @Mapping(target = "expenseId", source = "expense.id")
    @Mapping(target = "fromStatus", expression = "java(history.getFromStatus() != null ? history.getFromStatus().name() : null)")
    @Mapping(target = "toStatus", expression = "java(history.getToStatus().name())")
    @Mapping(target = "actorId", source = "actor.id")
    @Mapping(target = "actorName", expression = "java(history.getActor().getFullName())")
    WorkflowHistoryDto toDto(WorkflowHistory history);

    List<WorkflowHistoryDto> toHistoryDtoList(List<WorkflowHistory> history);
}
