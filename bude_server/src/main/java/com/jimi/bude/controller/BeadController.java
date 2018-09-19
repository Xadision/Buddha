package com.jimi.bude.controller;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.jimi.bude.model.Bead;
import com.jimi.bude.model.vo.BeadDetailsVO;
import com.jimi.bude.model.vo.PageUtil;
import com.jimi.bude.service.BeadService;
import com.jimi.bude.util.FileUtil;
import com.jimi.bude.util.ResultUtil;

/**
 * 软件包管理控制层
 * @type BeadController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年8月28日
 */
public class BeadController extends Controller {

	BeadService beadService = BeadService.me;
	
	/**
	 * 上传文件
	 * @param file 文件
	 * @param alias 别名
	 * @param updateDescribe 更新描述
	 * @param md5 文件md5校验码
	 */
	public void upload(UploadFile file, String alias, String updateDescribe,String md5) {
		file = getFile();
		String fileName = file.getFileName();
		String filePath = file.getUploadPath();
		alias = getPara("alias");
		updateDescribe = getPara("updateDescribe");
		String headName = "";
		String faceName = "";
		String suffixTime = "";
		Integer firstCode = null;
		Integer secondCode = null;
		Integer debugCode = null;
		try {
			String [] array = fileName.split("_");
			String [] name = array[0].split("-");
			String [] version = array[1].split("\\.");
			headName = name[0];
			faceName = name[1];
			firstCode = Integer.valueOf(version[0]);
			secondCode = Integer.valueOf(version[1]);
			debugCode = Integer.valueOf(version[2]);
			suffixTime= array[2].split("\\.")[0].substring(0, 12);
		} catch (Exception e) {
			file.getFile().delete();
			renderJson(ResultUtil.failed(401,"fileName is not in the right format"));
			return ;
		}
		if (md5 == null || md5.trim().equals("") || md5.length() != 32) {
			file.getFile().delete();
			renderJson(ResultUtil.failed(401,"md5 is not in the right format"));
			return ;
		}
		ResultUtil result = beadService.upload(file.getFile(), fileName, filePath, headName, faceName, firstCode, secondCode, debugCode, suffixTime, alias, updateDescribe, md5);
		renderJson(result);
	}
	
	/**
	 * 下载软件包
	 * @param headName 项目名
	 * @param faceName 模块名
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 * @param debugCode 修正版本号
	 * @param suffixTime 后缀时间
	 */
	public void download(String headName, String faceName, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime) {
		String fileName = FileUtil.getFileName(headName, faceName, firstCode.toString(), secondCode.toString(), debugCode.toString(), suffixTime);
		String filePath = FileUtil.getFilePath("BEAD", headName, faceName);
		File file = FileUtil.findFile(filePath, fileName);
		if (file == null) {
			renderNull();
			//renderJson(ResultUtil.failed(501, "file is not exist"));
			return;
		}
		renderFile(file);
	}
	
	public void update(Integer beadId, String alias, String updateDescribe) {
		if (beadId == null) {
			renderJson(ResultUtil.failed(401, "BeadId can not be null"));
			return ;
		}
		ResultUtil result = beadService.update(beadId, alias, updateDescribe);
		renderJson(result);
	}
	/**
	 * 查询主版本号
	 * @param faceId 模块ID
 	 */
	public void selectFirstCode(Integer faceId) {
		if (faceId == null) {
			renderJson(ResultUtil.failed(401, "Parameters can not be null"));
			return ;
		}
		List<Bead> beads = beadService.selectFirstCode(faceId);
		renderJson(ResultUtil.succeed(beads));
	}
	
	/**
	 * 查询次版本号
	 * @param faceId 模块ID
	 * @param firstCode 主版本号
	 */
	public void selectSecondCode(Integer faceId, Integer firstCode) {
		if (faceId == null || firstCode == null) {
			renderJson(ResultUtil.failed(401, "Parameters can not be null"));
			return ;
		}
		List<Bead> beads = beadService.selectSecondCode(faceId, firstCode);
		renderJson(ResultUtil.succeed(beads));
	}
	
	/**
	 * 查询修正版本号
	 * @param faceId 模块ID
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 */
	public void selectDebugCode(Integer faceId, Integer firstCode, Integer secondCode) {
		if (faceId == null || firstCode == null || secondCode == null) {
			renderJson(ResultUtil.failed(401, "Parameters can not be null"));
			return ;
		}
		List<Bead> beads = beadService.selectDebugCode(faceId, firstCode, secondCode);
		renderJson(ResultUtil.succeed(beads));
	}
	
	/**
	 * 查询后缀时间
	 * @param faceId 模块ID
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 * @param debugCode 修正版本号
	 */
	public void selectSuffixTime(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode) {
		if (faceId == null || firstCode == null || secondCode == null || debugCode == null) {
			renderJson(ResultUtil.failed(401, "Parameters can not be null"));
			return ;
		}
		List<Bead> beads = beadService.selectSuffixTime(faceId, firstCode, secondCode, debugCode);
		renderJson(ResultUtil.succeed(beads));
	}
	
	/**
	 * 查询软件包信息
	 * @param faceId 模块ID
	 * @param firstCode 主版本号(可空)
	 * @param secondCode 次版本号(可空)
	 * @param debugCode 修正版本号(可空)
	 */
	public void select(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime, Integer currentPage, Integer pageSize) {
		if(faceId == null) {
			renderJson(ResultUtil.failed(401, "faceId can not be null"));
			return ;
		}
		PageUtil<BeadDetailsVO> beads = beadService.select(faceId, firstCode, secondCode, debugCode, suffixTime, currentPage, pageSize);
		renderJson(ResultUtil.succeed(beads));
	}
	
	/**
	 * 删除软件包
	 * @param beadId
	 */
	public void delete(Integer beadId) {
		if (beadId == null) {
			renderJson(ResultUtil.failed(401, "beadId can not be null"));
			return ;
		}
		ResultUtil result = beadService.delete(beadId);
		renderJson(result);
	}
}
