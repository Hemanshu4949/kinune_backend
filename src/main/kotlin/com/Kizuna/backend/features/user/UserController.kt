package com.kizuna.backend.features.user

import com.kizuna.backend.features.user.dto.UserDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserDto> {
        val user = userService.getUserById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{id}/fcm-token")
    fun updateFcmToken(@PathVariable id: UUID, @RequestBody payload: Map<String, String>): ResponseEntity<Void> {
        val token = payload["token"] ?: return ResponseEntity.badRequest().build()
        userService.updateFcmToken(id, token)
        return ResponseEntity.ok().build()
    }
}
