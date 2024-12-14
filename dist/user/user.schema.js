"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.UserSchema = void 0;
const mongoose_1 = require("mongoose");
exports.UserSchema = new mongoose_1.Schema({
    email: { type: String, unique: true, required: true },
    password: { type: String, required: true },
    resetToken: { type: String, default: null },
    resetTokenDate: { type: Date, default: null },
});
//# sourceMappingURL=user.schema.js.map