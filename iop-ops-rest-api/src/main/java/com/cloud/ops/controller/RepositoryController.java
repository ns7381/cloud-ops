package com.cloud.ops.controller;

import com.cloud.ops.entity.Resource.Repository;
import com.cloud.ops.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/resource/repository")
public class RepositoryController {

    @Autowired
	private RepositoryService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Repository add(@RequestBody Repository repository){
		return service.add(repository);
	}

    /**
     * 删除
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id){
        service.delete(id);
        return Boolean.TRUE;
    }

    /**
     * 获取所有分支
     * @return
     */
    @RequestMapping(value = "/{id}/branches", method = RequestMethod.GET)
    public List<String> getBranches(@PathVariable String id) {
        return service.getBranches(id);
    }

	/**
	 * 编辑
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public Repository edit(@RequestBody Repository repository){
		return service.edit(repository);
	}

	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<Repository> getAll(){
		return service.getAll();
	}

}
