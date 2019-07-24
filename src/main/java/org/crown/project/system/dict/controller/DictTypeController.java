package org.crown.project.system.dict.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.crown.common.annotation.Log;
import org.crown.common.enums.BusinessType;
import org.crown.common.utils.poi.ExcelUtils;
import org.crown.framework.enums.ErrorCodeEnum;
import org.crown.framework.model.ExcelDTO;
import org.crown.framework.responses.ApiResponses;
import org.crown.framework.utils.ApiAssert;
import org.crown.framework.web.controller.WebController;
import org.crown.framework.web.page.TableData;
import org.crown.project.system.dict.domain.DictType;
import org.crown.project.system.dict.service.IDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据字典信息
 *
 * @author Crown
 */
@Controller
@RequestMapping("/system/dict")
public class DictTypeController extends WebController<DictType> {

    private final String prefix = "system/dict/type";

    @Autowired
    private IDictTypeService dictTypeService;

    @RequiresPermissions("system:dict:view")
    @GetMapping
    public String dictType() {
        return prefix + "/type";
    }

    @PostMapping("/list")
    @RequiresPermissions("system:dict:list")
    @ResponseBody
    public ApiResponses<TableData<DictType>> list(DictType dictType) {
        startPage();
        List<DictType> list = dictTypeService.selectDictTypeList(dictType);
        return success(getTableData(list));
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:dict:export")
    @PostMapping("/export")
    @ResponseBody
    public ApiResponses<ExcelDTO> export(DictType dictType) {

        List<DictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtils<DictType> util = new ExcelUtils<>(DictType.class);
        return success(new ExcelDTO(util.exportExcel(list, "字典类型")));

    }

    /**
     * 新增字典类型
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @RequiresPermissions("system:dict:add")
    @PostMapping("/add")
    @ResponseBody
    public ApiResponses<Void> addSave(@Validated DictType dict) {
        ApiAssert.isTrue(ErrorCodeEnum.DICT_TYPE_EXIST.overrideMsg("字典类型[" + dict.getDictType() + "]已存在"), dictTypeService.checkDictTypeUnique(dict));
        dictTypeService.save(dict);
        return success();

    }

    /**
     * 修改字典类型
     */
    @GetMapping("/edit/{dictId}")
    public String edit(@PathVariable("dictId") Long dictId, ModelMap mmap) {
        mmap.put("dict", dictTypeService.getById(dictId));
        return prefix + "/edit";
    }

    /**
     * 修改保存字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @RequiresPermissions("system:dict:edit")
    @PostMapping("/edit")
    @ResponseBody
    public ApiResponses<Void> editSave(@Validated DictType dict) {
        ApiAssert.isTrue(ErrorCodeEnum.DICT_TYPE_EXIST.overrideMsg("字典类型[" + dict.getDictType() + "]已存在"), dictTypeService.checkDictTypeUnique(dict));
        dictTypeService.updateDictType(dict);
        return success();
    }

    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @RequiresPermissions("system:dict:remove")
    @PostMapping("/remove")
    @ResponseBody
    public ApiResponses<Void> remove(String ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return success();

    }

    /**
     * 查询字典详细
     */
    @RequiresPermissions("system:dict:list")
    @GetMapping("/detail/{dictId}")
    public String detail(@PathVariable("dictId") Long dictId, ModelMap mmap) {
        mmap.put("dict", dictTypeService.getById(dictId));
        mmap.put("dictList", dictTypeService.list());
        return "system/dict/data/data";
    }

    /**
     * 校验字典类型
     */
    @PostMapping("/checkDictTypeUnique")
    @ResponseBody
    public ApiResponses<Boolean> checkDictTypeUnique(DictType dictType) {
        return success(dictTypeService.checkDictTypeUnique(dictType));
    }
}
