package org.iplatform.example.service.service;

import java.util.List;

import org.iplatform.example.service.dao.AttachMapper;
import org.iplatform.example.util.domain.Attach;
import org.iplatform.example.util.domain.AttachQuery;
import org.iplatform.example.util.domain.BootstrapTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
@RestController
@RequestMapping("/api/v1/attach")
public class AttachService {
	
	@Autowired
	AttachMapper mapper;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public ResponseEntity<BootstrapTable> list(@RequestBody AttachQuery query) {
		
		BootstrapTable table = new BootstrapTable();
		try {
			PageHelper.startPage(query.getOffset(), query.getLimit());
			List<Attach> attachList = mapper.attachList(query);
			if(attachList != null && !attachList.isEmpty()){
				PageInfo<Attach> pageInfo = new PageInfo<Attach>(attachList);
				table.setTotal(pageInfo.getTotal());
				table.setRows(pageInfo.getList());
			}
			return new ResponseEntity<>(table, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(table, HttpStatus.OK);
		}
		
	}
	
	@RequestMapping(value="/", method = RequestMethod.PUT)
	public ResponseEntity<Attach> addAttach(@RequestBody Attach attach) {
		
		mapper.insert(attach);
		return new ResponseEntity<>(attach, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/{fileId}", method = RequestMethod.PUT)
	public ResponseEntity<Attach> updateAttach(@RequestBody Attach attach) {
		
		mapper.update(attach);
		return new ResponseEntity<>(attach, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/{fileId}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteAttach(@PathVariable(value = "fileId") String fileId) {
		
		mapper.delete(fileId);
		return new ResponseEntity<>(true, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<Attach> getAttach(@PathVariable(value = "fileId") String fileId) {
		
		Attach attach = mapper.attach(fileId);
		return new ResponseEntity<>(attach, HttpStatus.OK);
		
	}
	
}
