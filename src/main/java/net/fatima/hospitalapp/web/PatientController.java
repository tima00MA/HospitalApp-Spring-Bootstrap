package net.fatima.hospitalapp.web;


import lombok.AllArgsConstructor;
import net.fatima.hospitalapp.entities.Patient;
import net.fatima.hospitalapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@AllArgsConstructor
public class PatientController {

    @Autowired
    private PatientRepository PatientRepository;
    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        // dans servlet (JEE) c'est comme ca : int page=Integer.parseInt(request.getParameter("page")
                        @RequestParam(name="page",defaultValue="0") int p,
                        @RequestParam(name = "size",defaultValue ="4" ) int s,
                        @RequestParam(name = "keyword",defaultValue ="" ) String kw) {

        //Page<Patient> pagePatients = PatientRepository.findAll(PageRequest.of(page,size));
        Page<Patient> pagePatients = patientRepository.findByNomContains(kw,PageRequest.of(p,s));
        model.addAttribute("ListPatients",pagePatients.getContent());
        model.addAttribute("pages",new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage",p);
        model.addAttribute("keyword",kw);
        return "patients";
    }

    @GetMapping("/deletePatient")
    public String delete (@RequestParam(name = "id")Long id,
                          @RequestParam(name = "keyword",defaultValue = "")String keyword,
                          @RequestParam(name = "page", defaultValue = "0")int page){
        patientRepository.deleteById(id);
        return "redirect:/index?page="+page+"&keyword="+keyword;

    }
}
