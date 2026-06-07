import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BusinessPolicy, BusinessPolicyService } from '../business-policies/business-policy.service';
import { PolicyRequirement, PolicyRequirementService } from '../../core/services/policy-requirement.service';

@Component({
    selector: 'app-policy-requirements',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './policy-requirements.component.html'
})
export class PolicyRequirementsComponent implements OnInit {

    policies: BusinessPolicy[] = [];
    requirements: PolicyRequirement[] = [];

    selectedPolicyId = '';
    docsText = '';
    questionsText = '';

    editingId: string | null = null;
    loading = false;
    message = '';

    constructor(
        private businessPolicyService: BusinessPolicyService,
        private policyRequirementService: PolicyRequirementService
    ) { }

    ngOnInit(): void {
        this.loadPolicies();
        this.loadRequirements();
    }

    loadPolicies(): void {
        this.businessPolicyService.listar().subscribe({
            next: (data: BusinessPolicy[]) => this.policies = data,
            error: () => this.message = 'No se pudieron cargar las políticas.'
        });
    }

    loadRequirements(): void {
        this.loading = true;

        this.policyRequirementService.listar().subscribe({
            next: (data: PolicyRequirement[]) => {
                this.requirements = data;
                this.loading = false;
            },
            error: () => {
                this.message = 'No se pudieron cargar los requisitos.';
                this.loading = false;
            }
        });
    }

    save(): void {
        if (!this.selectedPolicyId) {
            this.message = 'Selecciona una política.';
            return;
        }

        const payload: PolicyRequirement = {
            policyId: this.selectedPolicyId,
            initialRequiredDocs: this.parseLines(this.docsText),
            initialQuestions: this.parseLines(this.questionsText)
        };

        const request$ = this.editingId
            ? this.policyRequirementService.actualizar(this.editingId, payload)
            : this.policyRequirementService.crear(payload);

        request$.subscribe({
            next: () => {
                this.message = this.editingId
                    ? 'Requisitos actualizados correctamente.'
                    : 'Requisitos creados correctamente.';

                this.clearForm();
                this.loadRequirements();
            },
            error: () => this.message = 'No se pudo guardar el requisito.'
        });
    }

    edit(item: PolicyRequirement): void {
        this.editingId = item.id || null;
        this.selectedPolicyId = item.policyId;
        this.docsText = item.initialRequiredDocs.join('\n');
        this.questionsText = item.initialQuestions.join('\n');
    }

    delete(id?: string): void {
        if (!id) {
            return;
        }

        this.policyRequirementService.eliminar(id).subscribe({
            next: () => {
                this.message = 'Requisito eliminado correctamente.';
                this.loadRequirements();
            },
            error: () => this.message = 'No se pudo eliminar el requisito.'
        });
    }

    clearForm(): void {
        this.editingId = null;
        this.selectedPolicyId = '';
        this.docsText = '';
        this.questionsText = '';
    }

    getPolicyName(policyId: string): string {
        return this.policies.find(policy => policy.id === policyId)?.nombre || policyId;
    }

    private parseLines(value: string): string[] {
        return value
            .split('\n')
            .map(line => line.trim())
            .filter(line => line.length > 0);
    }
}