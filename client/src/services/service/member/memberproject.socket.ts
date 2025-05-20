import { websocketService } from "@/services/configsocket/websocket";

export type MBMeCreateOrDeleteTodoVoteRequest = {
    idTodo: string;
    projectId: string;
    idMember: string;
  };

export class WebSocketMemberProject {
  connect(callback?: () => void) {
    websocketService.connect(callback);
  }

  
   getJoinTodoVote(callback: (data: MBMeCreateOrDeleteTodoVoteRequest) => void) {
    const topic = `/topic/join-todoVote`;
    websocketService.subscribe(topic, callback);
  }

  sendJoinTodoVote(payload: MBMeCreateOrDeleteTodoVoteRequest) {
    const destination = `/app/join-todoVote`;
    websocketService.sendMessage(destination, payload);
  }


  getOutTodoVoteMemberProject(callback: (data: MBMeCreateOrDeleteTodoVoteRequest) => void) {
    const topic = `/topic/out-todoVote`;
    websocketService.subscribe(topic, callback);
  }

  sendOutTodoVoteMemberProject(payload: MBMeCreateOrDeleteTodoVoteRequest) {
    const destination = `/app/out-todoVote`;
    websocketService.sendMessage(destination, payload);
  }

}

export const webSocketMemberProject = new WebSocketMemberProject();
