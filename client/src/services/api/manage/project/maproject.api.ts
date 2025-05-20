import {
  PREFIX_API_MANAGE_PROJECT,
  PREFIX_API_MANAGE_PROJECT_DEPARTMENT_FACILITY,
} from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface projectRequest extends PaginationParams {
  search: string
  status: string
  nameDepartment: string
  idUser:string 
}

export type projectResponse = ResponseList & {
  id?: string
  orderNumber: number
  nameProject: string
  codeProject: string
  nameDepartment: string
  status?: string
  idFacility: string
}

export type projectDetailResponse = {
  name: string
  code: string
  endTime: string
  startTime: string
  progress: number
  descriptions: string
  idCategory: string
  idMajorFacility: string
}

export interface createProjectRequest {
  nameProject: string
  codeProject: string
  idMajorFacility: string
  descriptions?: string
  startTime: string
  endTime: string
  idCategory: string
  listMembers: {
    name: string
    code: string
    email: string
    image?: string
    role: string
  }[]
}

export type pjDepartmentFacilityResponse = {
  nameDepartment: string
  nameFacility: string
  id: string
  nameMajor: string
}

export type facilityResponse = {
  id: string
  name: string
}

export type ProjectDetailSummaryResponse = {
  id: string,
  name: string,
  code: string,
  nameDepartment: string,
  startTime: number,
  endTime: number,
  status: string,
  createdDateProject: number,
  todos: {name: string, List: number}[]
}

export const getAllProject = async (params: projectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<projectResponse>>>>

  return res.data
}

export const getAllPJDepartFacility = async (idFacility: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT_DEPARTMENT_FACILITY}/${idFacility}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<pjDepartmentFacilityResponse>>
  return res.data
}

export const getAllFacilityProject = async () => {
  const res = (await request({
    url: `manage/project/facility`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<facilityResponse>>
  return res.data
}

export const detailProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectDetailResponse>>

  return res.data
}

export const updateProject = async (id: string, data: createProjectRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/${id}`,
    method: 'PUT',
    data: data
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data
}

export const getDetailSummaryProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/sumary/${id}`,
    method: 'GET',
  })) as AxiosResponse<DefaultResponse<ProjectDetailSummaryResponse>>

  return res.data;
}

export const finishEarlyProject = async (id: string) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_PROJECT}/finish-early/${id}`,
    method: 'GET'
  })) as AxiosResponse<DefaultResponse<projectResponse>>

  return res.data;
}

