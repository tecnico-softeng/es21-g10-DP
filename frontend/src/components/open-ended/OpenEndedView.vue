<template>
  <div class="open-ended-view">
    <p>
        <span>Maximum Characters: {{ maxC }}</span>
    </p>
    <p>
        <span>Proposed Answer: </span>
    </p>
    <p>
        <span>{{ pa }}</span>
    </p>
  </div>
</template>


<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';

@Component
export default class OpenEndedView extends Vue {
    @Prop() readonly questionDetails!: OpenEndedQuestionDetails;
    maxC: number = this.questionDetails.maxCharacters;
    pa: string = this.questionDetails.proposedAnswer;
}
</script>
