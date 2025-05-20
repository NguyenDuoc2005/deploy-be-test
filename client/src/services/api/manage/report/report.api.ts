import { PREFIX_API_MANAGE_REPORT } from '@/constants/url'
import request from '@/services/request'
import {
  DefaultResponse,
  PaginationParams,
  PaginationResponse,
  ResponseList
} from '@/types/api.common'
import { AxiosResponse } from 'axios'

export interface reportRequest extends PaginationParams {
  time?: number
  idProject: string
}

export type reportResponse = ResponseList & {
  id?: string
  nameStaff: string
  nameStudent: string
  imageStudent: string
  imageStaff: string
  workDoneToday: string
  workPlanTomorrow: string
  obstacles: string
  reportTime: string
  orderNumber: number
}

export const getReport = async (params: reportRequest) => {
  const res = (await request({
    url: `${PREFIX_API_MANAGE_REPORT}`,
    method: 'GET',
    params: params
  })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<reportResponse>>>>

  return res.data
}
