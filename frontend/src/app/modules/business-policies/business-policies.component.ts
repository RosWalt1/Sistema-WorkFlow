import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  BusinessPolicy,
  BusinessPolicyService,
  WorkflowConnection,
  WorkflowNode
} from './business-policy.service';

@Component({
  selector: 'app-business-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './business-policies.component.html'
})
export class BusinessPoliciesComponent implements OnInit {

  policies: BusinessPolicy[] = [];

  form: BusinessPolicy = this.nuevaPolicy();
  editando = false;
  selectedId: string | null = null;

  editorAbierto = false;
  policyEditor: BusinessPolicy | null = null;

  editMode = true;
  selectedNodeId: string | null = null;
  connectingFromId: string | null = null;

  draggingNode: WorkflowNode | null = null;
  dragStarted = false;

  lanes: string[] = ['Funcionario', 'Humano', 'Técnico'];

  constructor(private policyService: BusinessPolicyService) {}

  ngOnInit(): void {
    this.cargarPolicies();
  }

  nuevaPolicy(): BusinessPolicy {
    return {
      nombre: '',
      descripcion: '',
      diagramaJson: {
        nodes: [],
        connections: []
      }
    };
  }

  cargarPolicies(): void {
    this.policyService.listar().subscribe({
      next: data => this.policies = data,
      error: err => console.error('Error al listar políticas', err)
    });
  }

  guardar(): void {
    if (!this.form.nombre.trim()) return;

    if (this.editando && this.selectedId) {
      this.policyService.actualizar(this.selectedId, this.form).subscribe(() => {
        this.limpiarFormulario();
        this.cargarPolicies();
      });
    } else {
      this.policyService.crear(this.form).subscribe(() => {
        this.limpiarFormulario();
        this.cargarPolicies();
      });
    }
  }

  editar(policy: BusinessPolicy): void {
    this.editando = true;
    this.selectedId = policy.id || null;
    this.form = JSON.parse(JSON.stringify(policy));
  }

  abrirEditor(policy: BusinessPolicy): void {
    this.policyEditor = JSON.parse(JSON.stringify(policy));

    if (!this.policyEditor) return;

    if (!this.policyEditor.diagramaJson) {
      this.policyEditor.diagramaJson = { nodes: [], connections: [] };
    }

    if (!this.policyEditor.diagramaJson.nodes) {
      this.policyEditor.diagramaJson.nodes = [];
    }

    if (!this.policyEditor.diagramaJson.connections) {
      this.policyEditor.diagramaJson.connections = [];
    }

    this.lanes = this.obtenerCarrilesDesdeNodos();

    if (this.lanes.length === 0) {
      this.lanes = ['Funcionario', 'Humano', 'Técnico'];
    }

    this.editMode = true;
    this.editorAbierto = true;
    this.selectedNodeId = null;
    this.connectingFromId = null;
    this.draggingNode = null;
    this.dragStarted = false;

    this.normalizarNodos();
  }

  volverListado(): void {
    this.editorAbierto = false;
    this.policyEditor = null;
    this.selectedNodeId = null;
    this.connectingFromId = null;
    this.draggingNode = null;
    this.dragStarted = false;
  }

  activar(policy: BusinessPolicy): void {
    if (!policy.id) return;

    this.policyService.activar(policy.id).subscribe(() => {
      this.cargarPolicies();
    });
  }

  eliminar(policy: BusinessPolicy): void {
    if (!policy.id) return;

    if (confirm('¿Seguro que deseas eliminar esta política?')) {
      this.policyService.eliminar(policy.id).subscribe(() => {
        this.cargarPolicies();
      });
    }
  }

  limpiarFormulario(): void {
    this.editando = false;
    this.selectedId = null;
    this.form = this.nuevaPolicy();
  }

  agregarNodo(tipo: 'INICIO' | 'ACTIVIDAD' | 'DECISION' | 'FIN'): void {
    if (!this.policyEditor) return;

    const lane = this.lanes[0] || 'Sin responsable asignado';

    const node: WorkflowNode = {
      id: 'n' + Date.now(),
      tipo,
      nombre: this.nombrePorTipo(tipo),
      calle: lane,
      posicionX: 320 + (this.policyEditor.diagramaJson.nodes.length * 180),
      posicionY: this.getLaneY(lane) + 50,
      condiciones: ''
    };

    this.policyEditor.diagramaJson.nodes.push(node);
    this.selectedNodeId = node.id;
  }

  nombrePorTipo(tipo: string): string {
    if (tipo === 'INICIO') return 'Inicio';
    if (tipo === 'ACTIVIDAD') return 'Nueva actividad';
    if (tipo === 'DECISION') return 'Decisión';
    if (tipo === 'FIN') return 'Fin';
    return 'Nodo';
  }

  seleccionarNodo(node: WorkflowNode): void {
    if (this.dragStarted) {
      this.dragStarted = false;
      return;
    }

    this.selectedNodeId = node.id;

    if (this.connectingFromId && this.connectingFromId !== node.id) {
      this.crearConexion(this.connectingFromId, node.id);
      this.connectingFromId = null;
    }
  }

  iniciarConexion(node: WorkflowNode): void {
    this.connectingFromId = node.id;
    this.selectedNodeId = node.id;
  }

  crearConexion(origen: string, destino: string): void {
    if (!this.policyEditor) return;

    const existe = this.policyEditor.diagramaJson.connections.some(
      c => c.origen === origen && c.destino === destino
    );

    if (existe) return;

    const connection: WorkflowConnection = {
      origen,
      destino,
      condicion: ''
    };

    this.policyEditor.diagramaJson.connections.push(connection);
  }

  eliminarConexion(index: number): void {
    if (!this.policyEditor) return;
    this.policyEditor.diagramaJson.connections.splice(index, 1);
  }

  eliminarNodo(nodeId: string): void {
    if (!this.policyEditor) return;

    this.policyEditor.diagramaJson.nodes =
      this.policyEditor.diagramaJson.nodes.filter(n => n.id !== nodeId);

    this.policyEditor.diagramaJson.connections =
      this.policyEditor.diagramaJson.connections.filter(
        c => c.origen !== nodeId && c.destino !== nodeId
      );

    if (this.selectedNodeId === nodeId) {
      this.selectedNodeId = null;
    }
  }

  moverNodo(node: WorkflowNode, dx: number, dy: number): void {
    node.posicionX = Math.max(300, node.posicionX + dx);
    node.posicionY = Math.max(20, node.posicionY + dy);
    node.calle = this.getLaneByY(node.posicionY);
  }

  iniciarArrastre(event: MouseEvent, node: WorkflowNode): void {
    event.preventDefault();
    event.stopPropagation();

    this.draggingNode = node;
    this.selectedNodeId = node.id;
    this.dragStarted = false;
  }

  arrastrarNodo(event: MouseEvent): void {
    if (!this.draggingNode) return;

    if (event.buttons !== 1) {
      this.finalizarArrastre();
      return;
    }

    this.dragStarted = true;

    this.draggingNode.posicionX = Math.max(
      300,
      this.draggingNode.posicionX + event.movementX
    );

    this.draggingNode.posicionY = Math.max(
      20,
      this.draggingNode.posicionY + event.movementY
    );

    this.draggingNode.calle = this.getLaneByY(this.draggingNode.posicionY);
  }

  finalizarArrastre(): void {
    this.draggingNode = null;
  }

  normalizarNodos(): void {
    if (!this.policyEditor) return;

    this.policyEditor.diagramaJson.nodes.forEach(node => {
      if (node.posicionX < 300) {
        node.posicionX = 300;
      }

      if (node.posicionY < 20) {
        node.posicionY = this.getLaneY(node.calle) + 50;
      }

      node.calle = node.calle?.trim() || this.getLaneByY(node.posicionY);
    });
  }

  guardarDiagrama(): void {
    if (!this.policyEditor || !this.policyEditor.id) return;

    this.normalizarNodos();

    this.policyService.actualizar(this.policyEditor.id, this.policyEditor).subscribe({
      next: updated => {
        this.policyEditor = JSON.parse(JSON.stringify(updated));
        this.cargarPolicies();
        alert('Diagrama guardado correctamente');
      },
      error: err => {
        console.error('Error al guardar diagrama', err);
        alert('Error al guardar diagrama');
      }
    });
  }

  validarFlujo(): void {
    if (!this.policyEditor) return;

    const nodes = this.policyEditor.diagramaJson.nodes;
    const connections = this.policyEditor.diagramaJson.connections;

    const tieneInicio = nodes.some(n => n.tipo === 'INICIO');
    const tieneFin = nodes.some(n => n.tipo === 'FIN');

    if (!tieneInicio) {
      alert('El flujo debe tener un nodo INICIO');
      return;
    }

    if (!tieneFin) {
      alert('El flujo debe tener un nodo FIN');
      return;
    }

    if (nodes.length > 1 && connections.length === 0) {
      alert('El flujo debe tener al menos una conexión');
      return;
    }

    alert('Flujo válido');
  }

  get selectedNode(): WorkflowNode | null {
    if (!this.policyEditor || !this.selectedNodeId) return null;

    return this.policyEditor.diagramaJson.nodes.find(
      n => n.id === this.selectedNodeId
    ) || null;
  }

  getNodeName(id: string): string {
    if (!this.policyEditor) return id;

    return this.policyEditor.diagramaJson.nodes.find(n => n.id === id)?.nombre || id;
  }

  getNode(id: string): WorkflowNode | undefined {
    return this.policyEditor?.diagramaJson.nodes.find(n => n.id === id);
  }

  getLaneY(lane: string): number {
    const index = this.lanes.indexOf(lane);
    return Math.max(0, index) * 160;
  }

  getLaneByY(y: number): string {
    const index = Math.min(
      this.lanes.length - 1,
      Math.max(0, Math.floor(y / 160))
    );

    return this.lanes[index] || 'Sin responsable asignado';
  }

  obtenerCarrilesDesdeNodos(): string[] {
    if (!this.policyEditor) return [];

    const carriles = this.policyEditor.diagramaJson.nodes.map(
      n => n.calle?.trim() || 'Sin responsable asignado'
    );

    return Array.from(new Set(carriles));
  }

  contarNodosPorCarril(lane: string): number {
    if (!this.policyEditor) return 0;

    return this.policyEditor.diagramaJson.nodes.filter(
      n => n.calle === lane
    ).length;
  }

  getCanvasHeight(): number {
    return Math.max(520, this.lanes.length * 160);
  }

  getConnectionLine(connection: WorkflowConnection): string {
    const origen = this.getNode(connection.origen);
    const destino = this.getNode(connection.destino);

    if (!origen || !destino) return '';

    const x1 = origen.posicionX + 120;
    const y1 = origen.posicionY + 40;
    const x2 = destino.posicionX;
    const y2 = destino.posicionY + 40;

    const midX = (x1 + x2) / 2;

    return `M ${x1} ${y1} C ${midX} ${y1}, ${midX} ${y2}, ${x2} ${y2}`;
  }

  agregarCarril(): void {
    const nombre = prompt('Nombre del nuevo carril:');
    if (!nombre || !nombre.trim()) return;

    if (!this.lanes.includes(nombre.trim())) {
      this.lanes.push(nombre.trim());
    }
  }
  eliminarCarril(lane: string): void {
  if (!this.policyEditor) return;

  const nodosEnCarril = this.policyEditor.diagramaJson.nodes.filter(
    n => n.calle === lane
  );

  if (nodosEnCarril.length > 0) {
    alert('No puedes eliminar este carril porque tiene nodos.');
    return;
  }

  if (confirm(`¿Eliminar el carril "${lane}"?`)) {
    this.lanes = this.lanes.filter(l => l !== lane);
  }
}

getVistaTextualFlujo(): WorkflowConnection[] {
  if (!this.policyEditor) return [];
  return this.policyEditor.diagramaJson.connections;
}
}