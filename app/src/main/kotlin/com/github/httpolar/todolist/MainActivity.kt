package com.github.httpolar.todolist

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.github.httpolar.todolist.ui.theme.TodoListTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListScreen()
        }
    }
}

data class TaskData(
    val content: String = "",
    val id: UUID = UUID.randomUUID(),
)

data class TodoListState(
    val textInput: String = "",
    val search: String = "",
    val tasks: List<TaskData> = listOf(),
)

class TodoListViewModel : ViewModel() {
    private val _state = MutableStateFlow(TodoListState())
    val state = _state.asStateFlow()

    fun setTextInput(s: String) {
        _state.update { current ->
            current.copy(textInput = s)
        }
    }

    fun setSearch(s: String) {
        _state.update { current ->
            current.copy(search = s)
        }
    }

    fun createTask() {
        _state.update { current ->
            current.copy(
                tasks = buildList {
                    add(TaskData(current.textInput))
                    addAll(current.tasks)
                },
                textInput = "",
            )
        }
    }

    fun removeTask(id: UUID) {
        _state.update { current ->
            current.copy(tasks = current.tasks.filter { it.id != id })
        }
    }
}

@Preview
@Composable
fun TodoListScreen() {
    TodoListTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    UserInput()
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    Tasks()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListAppBar(viewModel: TodoListViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    TopAppBar(
        title = { Text(text = "Todo") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInput(viewModel: TodoListViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = state.textInput,
            onValueChange = viewModel::setTextInput,
            modifier = Modifier.weight(1f),
            label = { Text("Task") }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = viewModel::createTask) {
            Text(
                text = "Add",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Task(data: TaskData, viewModel: TodoListViewModel = viewModel()) {
    Card(
        onClick = { viewModel.removeTask(data.id) },
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = data.content)
        }
    }
}

@Composable
fun Tasks(viewModel: TodoListViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(state.tasks) { _, task ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Task(task)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
            }
        }
    }
}