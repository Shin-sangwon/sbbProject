package com.ll.exam.sbb.question;

import com.ll.exam.sbb.answer.AnswerForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor // 생성자 주입
public class QuestionController {
    // @Autowired // 필드 주입
    private final QuestionService questionService;

    @RequestMapping("/question/list")
    // 이 자리에 @ResponseBody가 없으면 resources/question_list/question_list.html 파일을 뷰로 삼는다.
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("paging", paging);

        return "question_list";
    }

    @RequestMapping("/question/detail/{id}")
    public String detail(Model model, @PathVariable Long id, AnswerForm answerForm){
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
    }

    @GetMapping("/question/create")
    //빈 값이라도 있어야 하므로 form 넣어주기
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PostMapping("/question/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        questionService.create(questionForm.getSubject(), questionForm.getContent());

        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
}