package io.nuvalence.datagpt.controller

import io.nuvalence.datagpt.domain.Answer
import io.nuvalence.datagpt.domain.AnswerRequest
import io.nuvalence.datagpt.service.QuestionAnswerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NaturalLanguageQuestionController(private val questionAnswerService: QuestionAnswerService) {

    @PostMapping("/answer")
    fun generateQuery(@RequestBody request: AnswerRequest): Answer {
        return questionAnswerService.answerQuestion(request.question, request.persona)
    }

}
