import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose'; 
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { UserSchema } from './user.schema'; 

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'User', schema: UserSchema }]), // Reg schema
  ],
  controllers: [UserController],
  providers: [UserService],
})
export class UserModule {}
