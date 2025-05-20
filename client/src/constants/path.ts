import Member from '@/pages/member/member.vue'
import { Components } from 'ant-design-vue/es/date-picker/generatePicker'

export const ROUTES_CONSTANTS = {
  LOGIN: {
    path: '/login',
    name: 'login'
  },

  // ADMIN
  ADMIN: {
    path: '/admin',
    name: 'admin',
    children: {
      STAFF: {
        path: 'staff',
        name: 'staff'
      },
      STAFF_DETAIL: {
        path: 'staff-detail/:id',
        name: 'staff-detail'
      },
      PROJECT: {
        path: 'project',
        name: 'project',
        children: {
          PROJECT_STATISTICS: {
            path: 'project-statistics',
            name: 'project-statistics'
          }
        }
      },
      ROLE: {
        path: 'role',
        name: 'role'
      },
      DEPARTMENT: {
        path: 'department',
        name: 'department'
      },
      FACILITY: {
        path: 'facility',
        name: 'facility'
      },
      DEPARTMENT_FACILITY: {
        path: 'department-facility',
        name: 'department-facility'
      },
      CATEGORY: {
        path: 'category',
        name: 'category'
      },
      STUDENT: {
        path: 'student',
        name: 'student'
      },
      STUDENT_DETAIL: {
        path: 'student-detail/:id',
        name: 'student-detail'
      }
    }
  },
  // MANAGE
  MANAGE: {
    path: '/manage',
    name: 'manage',
    children: {
      MANAGE: {
        path: 'ql',
        name: 'ql'
      },
      STATISTICS: {
        path: 'statistics/:id',
        name: 'statistics'
      },

      PROJECT: {
        path: 'maproject',
        name: 'maproject'
      },

      TODOLIST: {
        path: 'todolist/:id',
        name: 'todolist'
      },
      REPORT: {
        path: 'report/:id',
        name: 'report'
      },

      DESCRIBE: {
        path: 'describe/:id',
        name: 'describe'
      },
      SUMMARY: {
        path: 'summary/:id',
        name: 'summary'
      },
      TODO: {
        path: 'todo/:id',
        name: 'todo'
      },
      TODO_MODAL: {
        path: 'todo/:id/:idPhase',
        name: 'todo-modal'
      },

      PHASE: {
        path: 'phase/:id',
        name: 'phase'
      },

      MYPROJECT: {
        path: 'my-project',
        name: 'my-project'
      },

      NOCONTENT: {
        path: 'no-content/:id',
        name: 'no-content'
      },
      REPORT_DETAIL: {
        path: 'report-detail',
        name: 'report-detail'
      },
      PROJECT_DETAIL: {
        path: 'project-detail/:id/:idPhase',
        name: 'project-detail-manage',
        children: {
          TABLE: {
            path: 'table',
            name: 'table-manage'
          }
        }
      },
      LOG: {
        path: 'log/:id',
        name: 'log-member',
      }
    }
  },
  // MEMBER
  MEMBER: {
    path: '/member',
    name: 'member',
    children: {
      MEMBER: {
        path: 'mb',
        name: 'mb'
      },
      MYPROJECT: {
        path: 'my-project',
        name: 'my-project'
      },
      REPORT: {
        path: 'member-report/:id',
        name: 'member-report'
      },
      PHASE: {
        path: 'member-phase/:id',
        name: 'member-phase'
      },
      PHASE_PROJECT: {
        path: 'phase-project/:id',
        name: 'phase-project'
      },
      PROJECT_DETAIL: {
        path: 'project-detail/:id/:idPhase',
        name: 'project-detail-member',
        children: {
          TABLE: {
            path: 'table',
            name: 'table-member'
          }
        }
      },
      LOG: {
        path: 'log/:id',
        name: 'log-member',
      }
    }
  },
  PROJECT: {
    path: '/project',
    name: 'project',
    children: {
      PROJECT_DETAIL: {
        path: 'project-detail/:id/:idPhase',
        name: 'project-details',
        children: {
          TABLE: {
            path: 'table',
            name: 'table'
          }
        }
      },

      VOTE_TODO: {
        path: 'vote-todo/:id',
        name: 'vote-todo'
      },

      BOARD: {
        name: 'project-board',
        path: 'board'
      },
      TIMELINE: {
        name: 'project-timeline',
        path: 'timeline'
      },
      BACKLOG: {
        name: 'project-backlog',
        path: 'backlog'
      },
      ACTIVE_SPRINTS: {
        name: 'project-active-sprints',
        path: 'active-sprints'
      },
      CALENDAR: {
        name: 'project-calendar',
        path: 'calendar'
      },
      REPORTS: {
        name: 'project-reports',
        path: 'reports'
      }
    }
  },
  FORBIDDEN: {
    path: '/error/403',
    name: 'Forbidden'
  },

  UNAUTHORIZED: {
    path: '/error/401',
    name: 'Unauthorized'
  },

  NOT_FOUND: {
    path: '/:pathMatch(.*)*',
    name: 'NotFound'
  },

  REDIRECT: {
    path: '/redirect',
    name: 'redirect'
  }
}
