import { websocketService } from '@/services/configsocket/websocket'
export interface dataCreatePhase {
  name: string
  code: string
  startTime?: string
  endTime?: string
  idProject: string
}

export interface dataCreateTodoByPhase {
  idTodo: string[]
  idPhase: string
  idProject: string
  index: string
}

export class PhaseWebSocket {
  connect(callback?: () => void) {
    websocketService.connect(callback)
  }

  subscribeCreatePhase(callback) {
    const topic = `/topic/create-phase` // Kiểm tra kỹ đường dẫn này!
    console.log('📡 Đang lắng nghe tại:', topic)
    websocketService.subscribe(topic, (data) => {
      console.log('📬 Dữ liệu nhận được: ', data)
      callback(data)
    })
  }

  sendMessageCreatePhase(payload: dataCreatePhase) {
    const des = `/app/create-phase`
    websocketService.sendMessage(des, payload)
  }

  subscribeDeletePhase(callback) {
    const topic = `/topic/delete-phase` // Kiểm tra kỹ đường dẫn này!
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageDeletePhase(id: string) {
    const des = `/app/delete-phase/${id}`
    websocketService.sendMessage(des, null)
  }

  subscribeUpdatePhase(callback) {
    const topic = `/topic/update-phase`
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageUpdatePhase(id: string, payload: dataCreatePhase) {
    const des = `/app/update-phase/${id}`
    websocketService.sendMessage(des, payload)
  }

  subscribeUpdateStatusPhase(callback) {
    const topic = `/topic/update-status-phase`
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageUpdateStatusPhase(id: string, index: string) {
    const des = `/app/update-status-phase/${id}`
    websocketService.sendMessage(des, index)
  }

  subscribeCreateTodoByPhase(callback) {
    const topic = `/topic/create-todo-by-phase`
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageCreateTodoByPhase(payload: dataCreateTodoByPhase) {
    const des = `/app/create-todo-by-phase`
    websocketService.sendMessage(des, payload)
  }

  subscribeDeleteTodoByPhase(callback) {
    const topic = `/topic/delete-todo-by-phase`
    websocketService.subscribe(topic, (data) => {
      callback(data)
    })
  }

  sendMessageDeleteTodoByPhase(id: string) {
    const des = `/app/delete-todo-by-phase/${id}`
    websocketService.sendMessage(des, null)
  }
}

export const phaseWebSocket = new PhaseWebSocket()
