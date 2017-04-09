package com.cloud.ops.controller;

import com.cloud.ops.entity.application.WorkFlow;
import com.cloud.ops.service.WorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/workflows")
public class WorkFlowController {

    @Autowired
	private WorkFlowService service;

	/**
	 * 新增应用程序 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public WorkFlow create(@RequestBody WorkFlow workFlow){
		return service.save(workFlow);
	}

    /**
     * 删除应用程序
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id){
        service.delete(id);
        return Boolean.TRUE;
    }

	/**
	 * 编辑
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public WorkFlow edit(@RequestBody WorkFlow workFlow){
		return service.update(workFlow);
	}
	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<WorkFlow> getByObjectId(@RequestParam String objectId){
		return service.getByObjectId(objectId);
	}

}
