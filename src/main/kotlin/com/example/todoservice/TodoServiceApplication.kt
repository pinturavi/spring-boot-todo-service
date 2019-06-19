package com.example.todoservice

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.Valid
import javax.validation.constraints.Size

@SpringBootApplication
class TodoServiceApplication(val tr: TodoRepository) {

    @Bean
    fun commandLineRunner() = CommandLineRunner {
        tr.saveAll(
                listOf(
                        Todo(description = "todo 0"),
                        Todo(description = "todo 1"),
                        Todo(description = "todo 2")
                )
        )
    }
}

fun main(args: Array<String>) {
    runApplication<TodoServiceApplication>(*args)
}


@Entity
data class Todo(
        @Id
        @GeneratedValue
        var id: Long = 0,

        @Size(min = 3, message = "description size should be atleast 3")
        var description: String
)

interface TodoRepository : JpaRepository<Todo, Long>

@RestController
@RequestMapping("/api/todos")
class TodoController(val tr: TodoRepository) {

    @GetMapping
    fun retrieveAllTodos() = tr.findAll()

    @GetMapping("/{id}")
    fun retrieveTodoById(@PathVariable("id") id: Long) = tr.findById(id).map { it }.orElseThrow { TodoNotFoundException("") }

	@PostMapping
	fun addTodo(@RequestBody @Valid todo:Todo) = tr.save(todo)

	@PutMapping
	fun updateTodo(@RequestBody @Valid todo:Todo) = tr.save(todo)

	@DeleteMapping("/{id}")
	fun deleteTodo(@PathVariable("id") id:Long) = tr.deleteById(id)

}

@ResponseStatus(HttpStatus.NOT_FOUND)
class TodoNotFoundException(s: String) : RuntimeException()
