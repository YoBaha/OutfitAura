import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.useGlobalPipes(new ValidationPipe()); // This enables validation globally
  
  // Enable CORS to allow cross-origin requests from your Android device
  app.enableCors();

  await app.listen(3000, '0.0.0.0');
}
bootstrap();
