package tn.esprit.outfitaura2.viewmodels
import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class StyleClassifier(modelPath: String, assetManager: AssetManager) {

    private val interpreter: Interpreter

    init {
        // Load and initialize the TFLite interpreter
        val modelBuffer = loadModelFile(assetManager, modelPath)
        interpreter = Interpreter(modelBuffer)
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        assetManager.openFd(modelPath).use { assetFileDescriptor ->
            FileInputStream(assetFileDescriptor.fileDescriptor).use { fileInputStream ->
                val fileChannel = fileInputStream.channel
                return fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.declaredLength
                )
            }
        }
    }

    fun classify(image: Bitmap): Int {
        // Preprocess the image
        val inputBuffer = preprocessImage(image)

        // Define the output buffer for the model's predictions
        val outputBuffer = TensorBuffer.createFixedSize(
            intArrayOf(1, 4), // Adjust dimensions to match style model output
            org.tensorflow.lite.DataType.FLOAT32
        )

        // Run the model inference
        interpreter.run(inputBuffer, outputBuffer.buffer)

        // Find the index of the highest probability (classification result)
        return outputBuffer.floatArray.indices.maxByOrNull { outputBuffer.floatArray[it] } ?: -1
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 224 // Standard input size for most TFLite models
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        // Create a ByteBuffer for normalized pixel data
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = resizedBitmap.getPixel(x, y)
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
                byteBuffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
            }
        }

        return byteBuffer
    }
}
