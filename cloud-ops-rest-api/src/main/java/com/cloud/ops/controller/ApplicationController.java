package com.cloud.ops.controller;

import com.cloud.ops.entity.Application;
import com.cloud.ops.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/application")
public class ApplicationController {

    @Autowired
	private ApplicationService service;

	/**
	 * 新增应用程序 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Application addApp(@RequestBody Application application){
		return service.addApp(application);
	}

    /**
     * 删除应用程序
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id){
        service.deleteApp(id);
        return Boolean.TRUE;
    }

	/**
	 * 编辑
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public Application edit(@RequestBody Application application){
		return service.editApp(application);
	}

	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<Application> getAll(){
		return service.getAll();
	}

}
