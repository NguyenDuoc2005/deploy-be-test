import { defineStore } from 'pinia'

export const useFilterStore = defineStore('filter', {
  state: () => ({
    memberId: '',
    noMember: false,
    membersSelected: [] as string[],
    myTodo: false,
    dueDate: [] as string[],
    labels: [] as string[], 
    labelsSelected: [] as string[],  
    searchText: ''
  }),

  actions: {
    toggleNoMember() {
      this.noMember = !this.noMember
    },
    toggleMyTodo() {
      this.myTodo = !this.myTodo
    },
    setDueDate(dueDate: string[]) {
      this.dueDate = dueDate
    },
    toggleLabel(label: string) {
      const index = this.labels.indexOf(label)
      if (index === -1) {
        this.labels.push(label)
      } else {
        this.labels.splice(index, 1)
      }
    },
    setLabelsSelected(labels: string[]) {
      this.labelsSelected = labels
    },
    setSearchText(searchText: string) {
      this.searchText = searchText
    },
    resetFilters() {
      this.noMember = false
      this.dueDate = []
      this.labels = []
      this.labelsSelected = [] 
      this.searchText = ''
    }
  },
})
