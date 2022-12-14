package com.ll.exam.sbb.answer;

import com.ll.exam.sbb.question.Question;
import com.ll.exam.sbb.question.QuestionService;
import com.ll.exam.sbb.user.SiteUser;
import com.ll.exam.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
       Answer answer = answerService.create(question, answerForm.getContent(), siteUser);
        return "redirect:/question/detail/%d#answer_%d".formatted(answer.getQuestion().getId(), answer.getId());

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyAnswer(AnswerForm answerForm, @PathVariable("id") Long id, Principal principal) {
        Answer answer = answerService.getAnswer(id);

        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "?????? ????????? ????????????.");
        }

        answerForm.setContent(answer.getContent());

        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyAnswer(@Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Long id){

        if(bindingResult.hasErrors()) {
            return "answer_form";
        }

        Answer answer = answerService.getAnswer(id);

        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "?????? ????????? ????????????.");
        }

        answerService.modify(answer, answerForm.getContent());

        return "redirect:/question/detail/%d#answer_%d".formatted(answer.getQuestion().getId(), answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteAnswer(Principal principal, @PathVariable("id") Long id){
        Answer answer = answerService.getAnswer(id);

        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "?????? ????????? ????????????.");
        }

        answerService.delete(answer);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(@PathVariable("id") Long id, Principal principal) {

        Answer answer = answerService.getAnswer(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        answerService.vote(answer, siteUser);

        return "redirect:/question/detail/%d#answer_%d".formatted(answer.getQuestion().getId(), answer.getId());

    }
}
