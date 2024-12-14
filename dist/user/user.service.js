"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.UserService = void 0;
const common_1 = require("@nestjs/common");
const mongoose_1 = require("@nestjs/mongoose");
const mongoose_2 = require("mongoose");
const bcrypt = require("bcrypt");
let UserService = class UserService {
    constructor(userModel) {
        this.userModel = userModel;
    }
    async createUser(email, password) {
        try {
            const existingUser = await this.userModel.findOne({ email }).exec();
            if (existingUser) {
                return { success: false, message: 'User already exists' };
            }
            const hashedPassword = await bcrypt.hash(password, 10);
            const newUser = new this.userModel({ email, password: hashedPassword });
            await newUser.save();
            return { success: true, message: 'User created successfully' };
        }
        catch (error) {
            return { success: false, message: 'Error creating user: ' + error.message };
        }
    }
    async validateUser(email, password) {
        try {
            const user = await this.userModel.findOne({ email }).exec();
            if (user && user.password && (await bcrypt.compare(password, user.password))) {
                return { success: true, user };
            }
            return { success: false, message: 'Invalid credentials' };
        }
        catch (error) {
            return { success: false, message: 'Error validating user: ' + error.message };
        }
    }
};
exports.UserService = UserService;
exports.UserService = UserService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, mongoose_1.InjectModel)('User')),
    __metadata("design:paramtypes", [mongoose_2.Model])
], UserService);
//# sourceMappingURL=user.service.js.map