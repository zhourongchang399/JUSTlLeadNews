package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/14 0:41
 */
@RestController
@RequestMapping("/api/v1/channel")
public class ChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult getAllChannels() {
        return wmChannelService.getAllChannels();
    }

    @PostMapping("/list")
    public ResponseResult ListChannels(@RequestBody WmChannelDto wmChannelDto) {
        return wmChannelService.list(wmChannelDto);
    }

    @PostMapping("/save")
    public ResponseResult saveChannel(@RequestBody WmChannel wmChannel) {
        return wmChannelService.save(wmChannel);
    }

    @GetMapping("/del/{id}")
    public ResponseResult delChannel(@PathVariable Long id) {
        return wmChannelService.delete(id);
    }

    @PostMapping("/update")
    public ResponseResult updateChannel(@RequestBody WmChannel wmChannel) {
        return  wmChannelService.update(wmChannel);
    }

}
