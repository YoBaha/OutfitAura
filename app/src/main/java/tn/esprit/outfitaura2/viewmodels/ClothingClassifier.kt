package tn.esprit.outfitaura2.viewmodels


import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClothingClassifier(modelPath: String, assetManager: AssetManager) {

    private val interpreter: Interpreter

    init {
        // Load the model from assets
        val modelBuffer = loadModelFile(assetManager, modelPath)
        interpreter = Interpreter(modelBuffer)
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = assetManager.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(image: Bitmap): Int {
        // Preprocess the image
        val inputBuffer = preprocessImage(image)

        // Define the output buffer for the predictions
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 11), org.tensorflow.lite.DataType.FLOAT32)

        // Run the model
        interpreter.run(inputBuffer, outputBuffer.buffer)

        // Get the index of the highest probability class
        return outputBuffer.floatArray.indices.maxByOrNull { outputBuffer.floatArray[it] } ?: -1
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 224 // Model input size (width and height)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        // Create a ByteBuffer to store the normalized pixel data
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = resizedBitmap.getPixel(x, y)

                // Normalize RGB values to [0, 1]
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
                byteBuffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
            }
        }

        return byteBuffer
    }
}
