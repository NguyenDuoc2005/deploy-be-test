package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeNotificationService;
import udpm.hn.server.entity.MemberNotification;
import udpm.hn.server.entity.Notification;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeNotificationServiceImpl implements MBMeNotificationService {

    private final MBMeNotificationMemberRepository mbMeNotificationMemberRepository;

    private final MBMeNotificationRepository mbMeNotificationRepository;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeStaffProjectRepository mbMeStaffProjectRepository;

    @Override
    public ResponseObject<?> createNotification(MBMeCreateNotificationCommentRequest request) {
        Notification notification = new Notification();
        notification.setMemberIdCreated(request.getIdUser());
        notification.setUrl(request.getUrl());

        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());

        if(todoFind.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"khoogn tìm thấy todo");
        }

        notification.setTodo(todoFind.get());
        notification.setName(request.getUsername() + " đã nhắc đến bạn trong một bình luận");
        Notification newNotification = mbMeNotificationRepository.save(notification);

        List<String> mentionedIds = request.getMentionedIds();
        List<MemberNotification> newList = new ArrayList<>();
        mentionedIds.forEach(idUserTag -> {
            if (!idUserTag.equals(request.getIdUser())) {
               Optional<String > idStaffProject =
                mbMeStaffProjectRepository.findIdStaffProjectByIdUserAndIdProject(idUserTag, request.getIdProject());
                StaffProject staffProjectFind =
                mbMeStaffProjectRepository.findById(idStaffProject.get()).orElse(null);
                MemberNotification notificationMember = new MemberNotification();
                notificationMember.setNotification(newNotification);
                notificationMember.setStaffProject(staffProjectFind);
                notificationMember.setMemberId(idUserTag);
                notificationMember.setStatus(EntityStatus.ACTIVE);
                newList.add(notificationMember);
            }
        });

        mbMeNotificationMemberRepository.saveAll(newList);

        return new ResponseObject<>(null,HttpStatus.OK,"Thêm thành công thng báo ");
    }
}
