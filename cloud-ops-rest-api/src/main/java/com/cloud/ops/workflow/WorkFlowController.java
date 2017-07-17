package com.cloud.ops.workflow;

import com.cloud.ops.esc.wf.WorkFlowService;
import com.cloud.ops.toscamodel.wf.WorkFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/workflows")
public class WorkFlowController {

    @Autowired
	private WorkFlowService service;

	@RequestMapping(method = RequestMethod.POST)
	public WorkFlow create(@RequestBody WorkFlow workFlow){
		return service.save(workFlow);
	}

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id){
        service.delete(id);
        return Boolean.TRUE;
    }

	@RequestMapping(method = RequestMethod.PUT)
	public WorkFlow edit(@RequestBody WorkFlow workFlow){
		return service.update(workFlow);
	}

	@RequestMapping(method = RequestMethod.GET )
	public List<WorkFlow> getByObjectId(@RequestParam String objectId){
		return service.getByObjectId(objectId);
	}

}
