import { Component, type OnInit } from "@angular/core"
import { FormBuilder, type FormGroup, Validators, ReactiveFormsModule } from "@angular/forms"
import { ActivatedRoute, Router } from "@angular/router"
import { CommonModule } from "@angular/common"
import TaskService from "../shared/tasks.service"
import type { Task } from "../shared/task.model"

@Component({
  selector: "app-update-task",
  standalone: true,
  templateUrl: "./updateTask.component.html",
  styleUrls: ["./updateTask.component.css"],
  imports: [CommonModule, ReactiveFormsModule],
})
export class UpdateTaskComponent implements OnInit {
  taskForm!: FormGroup
  taskId!: string
  isLoading = true
  errorMessage = ""
  originalTask: any = null

  difficultyOptions = ["HIGH", "MEDIUM", "LOW"]
  statusOptions = ["ACCEPTED", "PENDING", "DECLINED", "DEACTIVATED"]

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initForm()
    this.taskId = this.route.snapshot.paramMap.get("id")!
    console.log("Task ID from route:", this.taskId)

    if (!this.taskId) {
      this.errorMessage = "No task ID provided"
      this.isLoading = false
      return
    }
    this.loadTaskData()
  }

  initForm(): void {
    this.taskForm = this.fb.group({
      title: ["", Validators.required],
      description: [""],
      payout: [0, [Validators.required, Validators.min(0)]],
      budget: [0, [Validators.required, Validators.min(0)]],
      link: [""],
      difficulty: ["MEDIUM", Validators.required],
      status: ["PENDING"],
    })
  }

  async loadTaskData(): Promise<void> {
    try {
      this.isLoading = true
      this.errorMessage = ""
      console.log("Loading task with ID:", this.taskId)

      let foundTask = null

      // Skip the individual task endpoint since it doesn't exist
      // Try to get tasks from creator first
      try {
        const creatorResponse = await TaskService.getTasksFromCreator(1)
        console.log("Creator response:", creatorResponse)

        if (creatorResponse && creatorResponse.tasks && Array.isArray(creatorResponse.tasks)) {
          foundTask = creatorResponse.tasks.find((task: any) => task.id === Number(this.taskId))
          console.log("Found task from creator:", foundTask)
        }
      } catch (error) {
        console.log("Failed to get tasks from creator, trying all tasks...")
      }

      // If not found in creator tasks, try getting all tasks
      if (!foundTask) {
        try {
          const allTasksResponse = await TaskService.getAllTasks()
          console.log("All tasks response:", allTasksResponse)

          let tasks = []
          if (Array.isArray(allTasksResponse)) {
            tasks = allTasksResponse
          } else if (allTasksResponse?.tasks && Array.isArray(allTasksResponse.tasks)) {
            tasks = allTasksResponse.tasks
          }
          foundTask = tasks.find((task: any) => task.id === Number(this.taskId))
          console.log("Found task from all tasks:", foundTask)
        } catch (error) {
          console.error("Failed to get all tasks:", error)
        }
      }

      if (foundTask) {
        console.log("Successfully found task:", foundTask)
        this.originalTask = foundTask
        this.populateForm(foundTask)
      } else {
        this.errorMessage = `Task with ID ${this.taskId} not found`
        console.error("Task not found in any endpoint")
      }
    } catch (error) {
      console.error("Error loading task:", error)
      this.errorMessage = "Failed to load task data. Please try again."
    } finally {
      this.isLoading = false
    }
  }

  populateForm(task: any): void {
    this.taskForm.patchValue({
      title: task.title || "",
      description: task.description || "",
      payout: task.payout || 0,
      budget: task.budget || 0,
      link: task.link || "",
      difficulty: task.difficulty || "MEDIUM",
      status: task.status || "PENDING",
    })
  }

  async onSubmit(): Promise<void> {
    if (!this.taskForm.valid) {
      this.markFormGroupTouched()
      return
    }

    try {
      this.isLoading = true
      this.errorMessage = ""

      const formValues = this.taskForm.value
      const updatedTask: Task = {
        id: Number(this.taskId),
        title: formValues.title,
        description: formValues.description,
        payout: formValues.payout,
        budget: formValues.budget,
        link: formValues.link,
        difficulty: formValues.difficulty,
        image: this.originalTask?.image || "", // Use original image if available
        status: formValues.status,
        creator: this.originalTask?.creator || {
          id: 1,
          username: "DefaultUser",
          virtualMoney: 100,
        },
      }

      console.log("Updating task with data:", updatedTask)
      const response = await TaskService.updateTask(this.taskId, updatedTask)
      console.log("Task updated successfully:", response)
      await this.router.navigate(["/task-home"])
    } catch (error) {
      console.error("Error updating task:", error)
      this.errorMessage = "Failed to update task. Please try again."
    } finally {
      this.isLoading = false
    }
  }

  markFormGroupTouched(): void {
    Object.keys(this.taskForm.controls).forEach((key) => {
      this.taskForm.get(key)?.markAsTouched()
    })
  }

  cancel(): void {
    this.router.navigate(["/task-home"])
  }

  hasError(fieldName: string): boolean {
    const field = this.taskForm.get(fieldName)
    return !!(field && field.invalid && field.touched)
  }

  getErrorMessage(fieldName: string): string {
    const field = this.taskForm.get(fieldName)
    if (field && field.errors && field.touched) {
      if (field.errors["required"]) {
        return `${fieldName} is required`
      }
      if (field.errors["min"]) {
        return `${fieldName} must be greater than or equal to ${field.errors["min"].min}`
      }
    }
    return ""
  }
}