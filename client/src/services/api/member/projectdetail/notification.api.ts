import { PREFIX_API_NOTIFICATION_PROJECT_DETAILS } from "@/constants/url";
import request from "@/services/request";
import { DefaultResponse } from "@/types/api.common"
import { AxiosResponse } from "axios";

export interface MBMeCreateNotificationCommentRequest {
    mentionedEmails?: string[];
    mentionedIds?:string[];
    idProject?: string;
    todoId: string;
    url: string;
    idUser: string;
    email?: string;
    username: string;
}

export const createNotificationTag = async (data: MBMeCreateNotificationCommentRequest) => {
    const res = (await request({
        url: `${PREFIX_API_NOTIFICATION_PROJECT_DETAILS}`,
        method: "POST",
        data: data
    })) as AxiosResponse<DefaultResponse<Array<null>>>;
    return res.data;
};
