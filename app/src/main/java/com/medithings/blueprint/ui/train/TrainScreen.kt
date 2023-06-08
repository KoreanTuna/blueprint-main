package com.medithings.blueprint.ui.train

import TransferLearningHelper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.tensorflow.lite.support.label.Category

@Composable
fun TrainScreen(
    viewModel: TrainViewModel = hiltViewModel(),
) {
    val transferLearningHelper = TransferLearningHelper(
        context = LocalContext.current,
        classifierListener = object : TransferLearningHelper.ClassifierListener {
            override fun onError(error: String) {
            }

            override fun onResults(results: List<Category>?, inferenceTime: Long) {
            }

            override fun onLossResults(lossNumber: Float) {
            }

            override fun onTrainSuccess() {
            }

            override fun onResult(result: Float) {

            }
        }
    )

    viewModel.numThreads.observe(LocalLifecycleOwner.current) {
        transferLearningHelper.numThreads = it
        transferLearningHelper.close()
        if (viewModel.getTrainingState() != TrainViewModel.TrainingState.PREPARE) {
            // If the model is training, continue training with old image
            // sets.
            viewModel.setTrainingState(TrainViewModel.TrainingState.TRAINING)
            transferLearningHelper.startTraining()
        }
    }
    //val trainingState = viewModel.trainingState.observeAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(1.dp))
        OutlinedButton(onClick = {
            viewModel.setTrainingState(TrainViewModel.TrainingState.TRAINING)
            transferLearningHelper.startTraining()
        }) {
            Text(text = "훈련 시작", style = MaterialTheme.typography.button)
        }
        Spacer(Modifier.height(DefaultPadding))
    }
}

private val DefaultPadding = 12.dp