import { PREFIX_API_MYPROJECT_MEMBER } from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface ParamsGetTodoProject extends PaginationParams {
    q?: string | '',
    name: string | null
}

export interface ParamsGetReport extends PaginationParams {
    q?: string | '',
    reportTime: number | null
}

export type TodoProjectResponse = ResponseList & {
    orderNumber: number,
    createdDate: number,
    nameTodo: string,
    projectId: string,
    priorityLevel: number,
    startTime: number,
    endTime: number
}

export type ReportResponse = ResponseList & {
    idReport: string
    createDate: number
    reportTime: number
    statusReport: string
    obstaclesReport: string
    wordPlanTomorrowReport: string
    wordDoneTodayReport:string
    idProject: string
}

export interface ReportRequest{
    workDoneToday: string
    obstacles: string
    workPlanTomorrow: string
    statusReport: string
    reportTimem: number
    idTodo: string
}

export const getAllReport = async ( params: ParamsGetReport,id: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/report/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<ReportResponse>>>>
    return res.data
}


export const getTodoByProject = async (params: ParamsGetTodoProject, id: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/todo/${id}`,
        method: 'GET',
        params: params
    })) as AxiosResponse<DefaultResponse<PaginationResponse<Array<TodoProjectResponse>>>>
    return res.data
}

export const addReport= async (data: ReportRequest, idProject: string) => {
    const res = (await request({
        url: `${PREFIX_API_MYPROJECT_MEMBER}/add-report/${idProject}`,
        method: 'POST',
        data: data
    })) as AxiosResponse<DefaultResponse<ReportResponse>>

    return res.data
}

export const updateReport = async (data: ReportRequest, id: string) => {
    const res = (await request({
      url: `${PREFIX_API_MYPROJECT_MEMBER}/update-report/${id}`,
      method: 'PUT',
      data: data
    })) as AxiosResponse<DefaultResponse<ReportResponse>>
  
    return res.data
}

export const detailReport = async (id: string, idProject: string) => {
    const res = (await request({
      url: `${PREFIX_API_MYPROJECT_MEMBER}/detail-report/${idProject}/${id}`,
      method: 'GET'
    })) as AxiosResponse<DefaultResponse<ReportResponse>>
  
    return res.data
}

export const getReportIdByDate = async (reportTime: number, idProject: string) => {
    const response = await request({
      url: `${PREFIX_API_MYPROJECT_MEMBER}/report-by-date/${reportTime}/${idProject}`,
      method: "GET",
    });
  
    return response.data;
};