<template>
  <div class="open-ended-create">
    <v-text-field
      v-model.number="sQuestionDetails.maxCharacters"
      :label="`Maximum characters allowed`"
      :data-cy="`Maximum characters allowed`"
      single-line
      type="number"
    ></v-text-field>
    <v-textarea
      v-model="sQuestionDetails.proposedAnswer"
      :label="`Proposed Answer`"
      :data-cy="`Proposed Answer`"
      :counter="sQuestionDetails.maxCharacters"
      :rules="[(v) => (v.length <= sQuestionDetails.maxCharacters) || 'Proposed answer is bigger than the given character limit!']"
      rows="4"
      auto-grow
      outlined
    ></v-textarea>
  </div>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';

@Component
export default class OpenEndedCreate extends Vue {
  @PropSync('questionDetails', { type: OpenEndedQuestionDetails })
  sQuestionDetails!: OpenEndedQuestionDetails;

}
</script>