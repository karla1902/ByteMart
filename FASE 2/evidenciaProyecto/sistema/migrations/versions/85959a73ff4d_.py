"""empty message

Revision ID: 85959a73ff4d
Revises: 00b2489f2d15
Create Date: 2024-08-16 12:58:05.217900

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '85959a73ff4d'
down_revision = '00b2489f2d15'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('imagen',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('image_url', sa.String(length=255), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('imagen')
    # ### end Alembic commands ###
