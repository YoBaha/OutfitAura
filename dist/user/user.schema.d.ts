import { Schema } from 'mongoose';
export declare const UserSchema: Schema<any, import("mongoose").Model<any, any, any, any, any, any>, {}, {}, {}, {}, import("mongoose").DefaultSchemaOptions, {
    email: string;
    password: string;
    resetToken: string;
    resetTokenDate: NativeDate;
}, import("mongoose").Document<unknown, {}, import("mongoose").FlatRecord<{
    email: string;
    password: string;
    resetToken: string;
    resetTokenDate: NativeDate;
}>> & import("mongoose").FlatRecord<{
    email: string;
    password: string;
    resetToken: string;
    resetTokenDate: NativeDate;
}> & {
    _id: import("mongoose").Types.ObjectId;
} & {
    __v: number;
}>;
