package com.ll.exam.sbb.question;

import com.ll.exam.sbb.answer.AnswerForm;
import com.ll.exam.sbb.user.SiteUser;
import com.ll.exam.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor // 생성자 주입
public class QuestionController {
    // @Autowired // 필드 주입
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/question/list")
    // 이 자리에 @ResponseBody가 없으면 resources/question_list/question_list.html 파일을 뷰로 삼는다.
    public String list(@RequestParam(defaultValue = "") String kw, @RequestParam(defaultValue = "") String sortCode, Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Question> paging = questionService.getList(kw, page, sortCode);

        // 미래에 실행된 question_list.html 에서
        // questionList 라는 이름으로 questionList 변수를 사용할 수 있다.
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);

        return "question_list";
    }

    @RequestMapping("/question/detail/{id}")
    public String detail(Model model, @PathVariable Long id, AnswerForm answerForm){
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/create")
    //빈 값이라도 있어야 하므로 form 넣어주기
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);

        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Long id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/delete/{id}")
    public String questionDelete(@PathVariable("id") Long id, Principal principal){

        Question question = questionService.getQuestion(id);

        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        questionService.delete(question);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/vote/{id}")
    public String questionVote(@PathVariable("id") Long id, Principal principal) {

        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.vote(question, siteUser);

        return "redirect:/question/detail/%d".formatted(id);

    }
}