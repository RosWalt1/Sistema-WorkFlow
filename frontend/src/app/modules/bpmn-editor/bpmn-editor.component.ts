import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import BpmnModeler from 'bpmn-js/lib/Modeler';
import { BusinessPolicy, BusinessPolicyService } from '../business-policies/business-policy.service';

@Component({
  selector: 'app-bpmn-editor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bpmn-editor.component.html'
})
export class BpmnEditorComponent implements AfterViewInit, OnDestroy {

  @ViewChild('canvas', { static: true }) canvas!: ElementRef;

  private modeler!: BpmnModeler;

  policyId = '';
  policy: BusinessPolicy | null = null;
  cargando = true;
  guardando = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private policyService: BusinessPolicyService
  ) {}

  ngAfterViewInit(): void {
    this.policyId = this.route.snapshot.paramMap.get('id') || '';

    this.modeler = new BpmnModeler({
      container: this.canvas.nativeElement
    });

    this.cargarPolitica();
  }

  cargarPolitica(): void {
    if (!this.policyId) return;

    this.policyService.obtenerPorId(this.policyId).subscribe({
      next: async policy => {
        this.policy = policy;

        const xml = policy.bpmnXml && policy.bpmnXml.trim()
          ? policy.bpmnXml
          : this.diagramaInicial();

        await this.modeler.importXML(xml);

        const canvas = this.modeler.get('canvas') as any;
        canvas.zoom('fit-viewport');

        this.cargando = false;
      },
      error: err => {
        console.error('Error al cargar política', err);
        this.cargando = false;
      }
    });
  }

  async guardar(): Promise<void> {
    if (!this.policy || !this.policy.id) return;

    this.guardando = true;

    try {
      const result = await this.modeler.saveXML({ format: true });

      const policyActualizada: BusinessPolicy = {
        ...this.policy,
        bpmnXml: result.xml || ''
      };

      this.policyService.actualizar(this.policy.id, policyActualizada).subscribe({
        next: updated => {
          this.policy = updated;
          this.guardando = false;
          alert('Diagrama BPMN guardado correctamente');
        },
        error: err => {
          console.error('Error al guardar diagrama', err);
          this.guardando = false;
          alert('Error al guardar el diagrama');
        }
      });
    } catch (error) {
      console.error('Error al generar XML BPMN', error);
      this.guardando = false;
      alert('Error al generar el XML BPMN');
    }
  }

  volver(): void {
    this.router.navigate(['/business-policies']);
  }

  diagramaInicial(): string {
    return `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
  id="Definitions_1"
  targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="false">
    <bpmn:startEvent id="StartEvent_1" name="Inicio" />
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="StartEvent_1_di" bpmnElement="StartEvent_1">
        <dc:Bounds x="160" y="120" width="36" height="36" />
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`;
  }

  ngOnDestroy(): void {
    if (this.modeler) {
      this.modeler.destroy();
    }
  }
}