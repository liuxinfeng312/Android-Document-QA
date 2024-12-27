package com.ml.shubham0204.docqa.domain.ocr
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.equationl.paddleocr4android.CpuPowerMode
import com.equationl.paddleocr4android.OCR
import com.equationl.paddleocr4android.OcrConfig
import com.equationl.paddleocr4android.bean.OcrResult
import com.equationl.paddleocr4android.callback.OcrInitCallback
import com.equationl.paddleocr4android.callback.OcrRunCallback

import org.koin.core.annotation.Single

//@Single
class OcrManager(private val context: Context) {
    private val TAG = "OcrManager"
    private val ocr: OCR = OCR(context)

    /**
     * 初始化 OCR 模型
     * @param onSuccess 成功回调
     * @param onError 失败回调
     */
    init {
        initModel(
            onSuccess = {
                println("模型初始化成功！")
            },
            onError = { error ->
                println("模型初始化失败：${error.message}")
            }
        )

    }
    fun initModel(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val config = OcrConfig().apply {
            modelPath = "models/ch_PP-OCRv2"
            clsModelFilename = "cls.nb"
            detModelFilename = "det_db.nb"
            recModelFilename = "rec_crnn.nb"
            isRunDet = true
            isRunCls = true
            isRunRec = true
            cpuPowerMode = CpuPowerMode.LITE_POWER_FULL

            isDrwwTextPositionBox = true
        }

        ocr.initModel(config, object : OcrInitCallback {
            override fun onSuccess() {
                Log.i(TAG, "OCR 初始化成功")
                onSuccess()
            }

            override fun onFail(e: Throwable) {
                Log.e(TAG, "OCR 初始化失败", e)
                onError(e)
            }
        })
    }

    /**
     * 运行 OCR 识别
     * @param bitmap 要识别的图像
     * @param onSuccess 成功回调，返回结果
     * @param onError 失败回调
     */
    fun runOcr(
        bitmap: Bitmap,
        onSuccess: (OcrResult) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        ocr.run(bitmap, object : OcrRunCallback {
            override fun onSuccess(result: OcrResult) {
                Log.i(TAG, "OCR 识别成功: ${result.simpleText}")
                onSuccess(result)
            }

            override fun onFail(e: Throwable) {
                Log.e(TAG, "OCR 识别失败", e)
                onError(e)
            }
        })
    }

    /**
     * 释放 OCR 模型
     */
    fun release() {
        ocr.releaseModel()
    }
}
