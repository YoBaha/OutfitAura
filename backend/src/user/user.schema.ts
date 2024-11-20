import { Schema } from 'mongoose';

export const UserSchema = new Schema({
  email: { type: String, unique: true, required: true },
  password: { type: String, required: true },
  resetToken: { type: String, default: null },
  resetTokenDate: { type: Date, default: null }, 
});
