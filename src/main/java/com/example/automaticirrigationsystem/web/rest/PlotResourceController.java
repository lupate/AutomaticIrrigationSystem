package com.example.automaticirrigationsystem.web.rest;


import com.example.automaticirrigationsystem.aop.logging.Loggable;
import com.example.automaticirrigationsystem.dto.PlotConfigDTO;
import com.example.automaticirrigationsystem.dto.PlotDTO;
import com.example.automaticirrigationsystem.exception.ResourceNotFoundException;
import com.example.automaticirrigationsystem.service.PlotService;
import com.example.automaticirrigationsystem.util.PaginationUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller for managing {@link com.example.automaticirrigationsystem.domain.Plot}.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class PlotResourceController {

  private final PlotService plotService;

  /**
   * {@code POST  /plots} : Create a new plot.
   *
   * @param plotDTO the plotDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   * plotDTO, or with status {@code 400 (Bad Request)} if the plot has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect. create expected: plotCode,
   *                            plotLength, plotWidth
   */

  @PostMapping("/plots")
  @Loggable
  public ResponseEntity<PlotDTO> createPlot(@Valid @RequestBody PlotDTO plotDTO)
      throws URISyntaxException {
    log.debug("REST request to save Plot : {}", plotDTO);

    PlotDTO result = plotService.save(plotDTO);
    return ResponseEntity
        .created(new URI("/api/plots/" + result.getId()))
        .body(result);
  }

  /**
   * {@code PUT  /plots/:id} : Updates an existing plot.
   *
   * @param id            the id of the plotDTO to save.
   * @param plotConfigDTO the plotConfigDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
   * plotDTO, or with status {@code 400 (Bad Request)} if the plotDTO is not valid, or with status
   * {@code 500 (Internal Server Error)} if the plotDTO couldn't be updated. configure expect: crop,
   * time slots count, water amount
   */
  @PutMapping("/plots/config/{id}")
  @Loggable
  public ResponseEntity<PlotDTO> configPlot(
      @PathVariable final Long id,
      @Valid @RequestBody PlotConfigDTO plotConfigDTO) {
    log.debug("REST request to configure Plot : {}, {}", id, plotConfigDTO);
    Optional<PlotDTO> result = plotService.configurePlot(plotConfigDTO, id);
    return result.map(plot -> ResponseEntity.ok().body(plot))
        .orElseThrow(() -> {
          throw new ResourceNotFoundException("plot doesn't exist");
        });
  }

  /**
   * {@code PUT  /plots/:id} : Updates an existing plot.
   *
   * @param id      the id of the plotDTO to save.
   * @param plotDTO the plotDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
   * plotDTO, or with status {@code 400 (Bad Request)} if the plotDTO is not valid, or with status
   * {@code 500 (Internal Server Error)} if the plotDTO couldn't be updated. update expect: crop,
   * time slots, water amount, plotCode, plotLength, plotWidth, sensor
   */

  @PutMapping("/plots/{id}")
  @Loggable
  public ResponseEntity<PlotDTO> updatePlot(
      @PathVariable final Long id,
      @Valid @RequestBody PlotDTO plotDTO) {
    log.debug("REST request to update Plot : {}, {}", id, plotDTO);

    plotDTO.setId(id);
    Optional<PlotDTO> result = plotService.partialUpdate(plotDTO);
    return result.map(plot -> ResponseEntity.ok().body(plot))
        .orElseThrow(() -> {
          throw new ResourceNotFoundException("Plot doesn't exist");
        });
  }

  /**
   * {@code GET  /plots} : get all the plots.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of plots in body.
   */
  @GetMapping("/plots")
  @Loggable
  public ResponseEntity<List<PlotDTO>> getAllPlots(
      @PageableDefault(sort = {"id"}) Pageable pageable) {
    log.debug("REST request to get a page of Plots");
    Page<PlotDTO> page = plotService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
        ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /plots/:id} : get the "id" plot.
   *
   * @param id the id of the plotDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the plotDTO, or
   * with status {@code 404 (Not Found)}.
   */
  @GetMapping("/plots/{id}")
  @Loggable
  public ResponseEntity<PlotDTO> getPlot(@PathVariable Long id) {
    log.debug("REST request to get Plot : {}", id);
    Optional<PlotDTO> plotDTO = plotService.findOne(id);
    return plotDTO.map(plot -> ResponseEntity.ok().body(plot))
        .orElseThrow(() -> {
          throw new ResourceNotFoundException("Plot doesn't exist");
        });
  }

  /**
   * {@code DELETE  /plots/:id} : delete the "id" plot.
   *
   * @param id the id of the plotDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/plots/{id}")
  @Loggable
  public ResponseEntity<Void> deletePlot(@PathVariable Long id) {
    log.debug("REST request to delete Plot : {}", id);
    plotService.delete(id);
    return ResponseEntity
        .noContent().build();
  }
}
