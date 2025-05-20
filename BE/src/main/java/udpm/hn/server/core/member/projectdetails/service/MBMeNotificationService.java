package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;

public interface MBMeNotificationService  {
    ResponseObject<?> createNotification(@Valid MBMeCreateNotificationCommentRequest request);
}
