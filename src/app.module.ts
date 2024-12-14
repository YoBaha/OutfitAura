import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import { UserModule } from './user/user.module';


@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27017/outfitaura'),  // Directly hardcoded URL
    UserModule,
  ],
})
export class AppModule {}
